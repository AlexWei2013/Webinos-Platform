module.exports = function(app, address, port) {
    var openid     = require('openid');
    var dependency = require("find-dependencies")(__dirname),
        logger     = dependency.global.require(dependency.global.util.location, "lib/logging.js")(__filename) || console,
        pzhadaptor = require('../pzhadaptor.js'),
        helper     = require('./helper.js');

    var attr = new openid.AttributeExchange({
        "http://axschema.org/contact/country/home":     "required",
        "http://axschema.org/namePerson/first":         "required",
        "http://axschema.org/pref/language":            "required",
        "http://axschema.org/namePerson/last":          "required",
        "http://axschema.org/contact/email":            "required",
        "http://axschema.org/namePerson/friendly":      "required",
        "http://axschema.org/namePerson":               "required",
        "http://axschema.org/media/image/default":      "required",
        "http://axschema.org/person/gender/":           "required"
    });

    app.get('/main/:useremail/request-access-login', function(req, res) {
        //Args: External user's PZH URL
        //Args: External user's PZH certificate
        //Auth: Check that the certificate is valid and that the certificate is valid for this URL.
        //UI: Presents a button that the user must click to confirm the request
        //UI: External user must then present an OpenID account credential...
        //sets req.externalprovider
        var externalCertUrl = req.query.certUrl;
        var externalPZHUrl = req.query.pzhInfo;

        res.render('login-remote',
            {externalCertUrl: encodeURIComponent(externalCertUrl),
                externalPZHUrl: encodeURIComponent(externalPZHUrl)});
    });

    app.get('/main/:useremail/request-access-authenticate', function(req, res) {
        //Args: External user's PZH URL
        //Args: External user's PZH certificate
        //Auth: Check that the certificate is valid and that the certificate is valid for this URL.
        //UI: Presents a button that the user must click to confirm the request
        //UI: External user must then present an OpenID account credential...

        var externalRelyingParty = new openid.RelyingParty(
            'https://'+address+':'+port +'/main/' +
                encodeURIComponent(req.params.useremail) +
                '/request-access-verify',
            null, false, false, [attr]);

        //'&externalCertUrl=' + encodeURIComponent(req.query.externalCertUrl) +  '&externalPZHUrl=' +  encodeURIComponent(req.query.externalPZHUrl)

        var identifierUrl =  (req.query.externalprovider === "google")? 'http://www.google.com/accounts/o8/id':
            'http://open.login.yahooapis.com/openid20/www.yahoo.com/xrds';

        externalRelyingParty.authenticate(identifierUrl, false, function(error, authUrl) {
            if (error)
            {
                res.writeHead(200);
                res.end('Authentication failed: ' + error.message);
            }
            else if (!authUrl)
            {
                res.writeHead(200);
                res.end('Authentication failed');
            }
            else
            {
                //this data needs to come with us on the next attempt...
                req.session.expectedExternalAuth = {
                    internalUser            : req.params.useremail,
                    externalCertUrl         : req.query.externalCertUrl,
                    externalPZHUrl          : req.query.externalPZHUrl,
                    externalRelyingParty    : req.query.externalprovider,
                    externalAuthUrl         : authUrl
                };
                res.writeHead(302, { Location: authUrl });
                res.end();
            }
        });
    });

    app.get('/main/:useremail/request-access-verify', function(req, res) {
        //Args: External user's PZH URL
        //Args: External user's PZH certificate
        //Auth: Check that the certificate is valid and that the certificate is valid for this URL.
        //Auth: OpenID credential.  THis is the redirect from a provider.
        //UI: Present some confirmation

        var externalRelyingParty = new openid.RelyingParty(
            'https://'+address+':'+port +'/main/' + encodeURIComponent(req.params.useremail)
                + '/request-access-verify',
            null, false, false, [attr]);

        externalRelyingParty.verifyAssertion(req, function(error, result) {
            if (error) {
                res.writeHead(200);
                res.end('Authentication failed');
            } else {
                logger.log("Successfully authenticated external user: " + util.inspect(result) +
                    "who claims to have: " + req.session.expectedExternalAuth.externalCertUrl +
                    " and " + req.session.expectedExternalAuth.externalPZHUrl);

                if (!req.session.expectedExternalAuth.externalCertUrl) {
                    res.writeHead(200);
                    res.end('Failed to read cookies');
                }

                var externalUrl = req.session.expectedExternalAuth.externalCertUrl;
                helper.getCertsFromHostDirect(externalUrl, function(certs) {
                    var pzhData = {
                        pzhCerts                : certs,
                        externalCertUrl         : req.session.expectedExternalAuth.externalCertUrl,
                        externalPZHUrl          : req.session.expectedExternalAuth.externalPZHUrl,
                        externalRelyingParty    : req.session.expectedExternalAuth.externalRelyingParty,
                        externalAuthUrl         : req.session.expectedExternalAuth.externalAuthUrl
                    };
                    pzhadaptor.setRequestingExternalUser(req.params.useremail, result, pzhData);
                    //TODO: Check we're dealing with the same _internal_ user
                    // This is for the same user...

                    res.render("external-request-success" ,
                        {externaluser: result, user: req.params.useremail,
                            externalPzhUrl: req.session.expectedExternalAuth.externalPZHUrl});
                    res.redirect(redirectUrl);
                }, function(err) {
                    res.writeHead(200);
                    res.end('Failed to retrieve certificate from remote host');
                });
            }
        });
    });
};