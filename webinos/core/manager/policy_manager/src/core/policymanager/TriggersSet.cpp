/*******************************************************************************
 *  Code contributed to the webinos project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Copyright 2012 Torsec -Computer and network security group-
 * Politecnico di Torino
 * 
 ******************************************************************************/

#include "TriggersSet.h"
#include "AuthorizationsSet.h"

TriggersSet::TriggersSet(TiXmlElement* triggersset){

	string p, purposes;
	for(unsigned int i=0; i<arraysize(ontology_vector); i++)
		purposes.append("0");

	// Trigger Tag
	if(triggersset->FirstChild("TriggerAtTime")){
		for(TiXmlElement * child = (TiXmlElement*)triggersset->FirstChild("TriggerAtTime"); child;
				child = (TiXmlElement*)child->NextSibling("TriggerAtTime") ) {
			trigger["triggerID"] = "TriggerAtTime";

			// read start parameter
			TiXmlElement * start = (TiXmlElement*)child->FirstChild("Start");
			if ((TiXmlElement*)start->FirstChild("StartNow"))
				trigger["Start"]="StartNow";
			if ((TiXmlElement*)start->FirstChild("DateAndTime"))
				trigger["Start"]=((TiXmlElement*)start->FirstChild("DateAndTime"))->GetText();

			// read MaxDelay parameter
			TiXmlElement * maxdelay = (TiXmlElement*)child->FirstChild("MaxDelay");
			trigger["MaxDelay"]=((TiXmlElement*)maxdelay->FirstChild("Duration"))->GetText();
			
			triggers.push_back(trigger);
			trigger.clear();
		}
	}
	if(triggersset->FirstChild("TriggerPersonalDataAccessedForPurpose")){
		for(TiXmlElement * child = (TiXmlElement*)triggersset->FirstChild("TriggerPersonalDataAccessedForPurpose"); child;
				child = (TiXmlElement*)child->NextSibling("TriggerPersonalDataAccessedForPurpose") ) {
			trigger["triggerID"] = "TriggerPersonalDataAccessedForPurpose";

			// read Purpose parameters
			for(TiXmlElement * purpose = (TiXmlElement*)child->FirstChild("Purpose"); purpose;
					purpose = (TiXmlElement*)child->NextSibling("Purpose") ) {
				p = ((TiXmlElement*)child->FirstChild("Purpose"))->GetText();
				for(unsigned int i = 0; i<arraysize(ontology_vector); i++){
					if (p.compare(ontology_vector[i]) == 0)
						purposes[i]='1';
				}
			}
			trigger["Purpose"]=purposes;
			for(unsigned int i=0; i<arraysize(ontology_vector); i++)
				purposes[i]='0';

			// read MaxDelay parameter
			TiXmlElement * maxdelay = (TiXmlElement*)child->FirstChild("MaxDelay");
			trigger["MaxDelay"]=((TiXmlElement*)maxdelay->FirstChild("Duration"))->GetText();
			
			triggers.push_back(trigger);
			trigger.clear();
		}
	}
	if(triggersset->FirstChild("TriggerPersonalDataDeleted")){
		for(TiXmlElement * child = (TiXmlElement*)triggersset->FirstChild("TriggerPersonalDataDeleted"); child;
				child = (TiXmlElement*)child->NextSibling("TriggerPersonalDataDeleted") ) {
			trigger["triggerID"] = "TriggerPersonalDataDeleted";

			// read MaxDelay parameter
			TiXmlElement * maxdelay = (TiXmlElement*)child->FirstChild("MaxDelay");
			trigger["MaxDelay"]=((TiXmlElement*)maxdelay->FirstChild("Duration"))->GetText();
			
			triggers.push_back(trigger);
			trigger.clear();
		}
	}
	if(triggersset->FirstChild("TriggerDataSubjectAccess")){
		for(TiXmlElement * child = (TiXmlElement*)triggersset->FirstChild("TriggerDataSubjectAccess"); child;
				child = (TiXmlElement*)child->NextSibling("TriggerDataSubjectAccess") ) {
			trigger["triggerID"] = "TriggerDataSubjectAccess";

			// read url parameter
			trigger["url"] = ((TiXmlElement*)child->FirstChild("url"))->GetText();
			
			triggers.push_back(trigger);
			trigger.clear();
		}
	}

}

TriggersSet::~TriggersSet(){
}

bool TriggersSet::evaluate(vector< map<string, string> > trig){
	bool purpose_satisfied, trigger_satisfied;
	tm start1, start2, delay1, delay2;
	time_t t_start1, t_start2, t_delay1, t_delay2;
	char sign;
	int millisec, off_hours, off_min;

	for(vector< map<string, string> >::iterator triggers_it=triggers.begin() ; triggers_it!=triggers.end() ; triggers_it++){
		trigger_satisfied = false;
		for(vector< map<string, string> >::iterator it=trig.begin() ; it!=trig.end() ; it++){

			// TriggerAtTime evaluation
			if((*triggers_it)["triggerID"] == (*it)["triggerID"] && (*triggers_it)["triggerID"] == "TriggerAtTime"){
				// Start is startNow or is the same time
				// MaxDelay from DH preference is greter or equal of application Max Delay
				if ((*triggers_it)["Start"] == (*it)["Start"] && (*triggers_it)["MaxDelay"] >= (*it)["MaxDelay"]){
					trigger_satisfied = true;
					break;
				}
				if ((*triggers_it)["Start"] != (*it)["Start"]){
					if((*triggers_it)["Start"] == "StartNow"){ 

						t_start1 = mktime(gmtime(NULL));
						sscanf((*triggers_it)["MaxDelay"].c_str(),"P%dY%dM%dDT%dH%dM%dS",
								&delay1.tm_year, &delay1.tm_mon, &delay1.tm_mday, &delay1.tm_hour, &delay1.tm_min, &delay1.tm_sec);
						t_delay1 = mktime(&delay1);
						
						sscanf((*it)["Start"].c_str(),"%d-%d-%dT%d:%d:%d.%d%c%d:%d",
								&start2.tm_year, &start2.tm_mon, &start2.tm_mday, &start2.tm_hour, &start2.tm_min, &start2.tm_sec, &millisec, &sign, &off_hours, &off_min);
						t_start2 = mktime(&start2);
						sscanf((*it)["MaxDelay"].c_str(),"P%dY%dM%dDT%dH%dM%dS",
								&delay2.tm_year, &delay2.tm_mon, &delay2.tm_mday, &delay2.tm_hour, &delay2.tm_min, &delay2.tm_sec);
						t_delay2 = mktime(&delay2);
						if (sign == '+')
							t_delay2 += 60*off_hours+off_min;
						else
							t_delay2 -= (60*off_hours+off_min);

						if (t_start1 > t_start2 && (t_start1+t_delay1 < t_start2+t_delay2)){
							trigger_satisfied = true;
							break;
						}
					}
					if((*triggers_it)["Start"] != "StartNow" && (*it)["Start"] != "StartNow"){ 
						sscanf((*triggers_it)["Start"].c_str(),"%d-%d-%dT%d:%d:%d.%d%c%d:%d",
								&start1.tm_year, &start1.tm_mon, &start1.tm_mday, &start1.tm_hour, &start1.tm_min, &start1.tm_sec, &millisec, &sign, &off_hours, &off_min);
						t_start1 = mktime(&start1);
						sscanf((*triggers_it)["MaxDelay"].c_str(),"P%dY%dM%dDT%dH%dM%dS",
								&delay1.tm_year, &delay1.tm_mon, &delay1.tm_mday, &delay1.tm_hour, &delay1.tm_min, &delay1.tm_sec);
						t_delay1 = mktime(&delay1);
						if (sign == '+')
							t_delay1 += 60*off_hours+off_min;
						else
							t_delay1 -= (60*off_hours+off_min);
						
						sscanf((*it)["Start"].c_str(),"%d-%d-%dT%d:%d:%d.%d%c%d:%d",
								&start2.tm_year, &start2.tm_mon, &start2.tm_mday, &start2.tm_hour, &start2.tm_min, &start2.tm_sec, &millisec, &sign, &off_hours, &off_min);
						t_start2 = mktime(&start2);
						sscanf((*it)["MaxDelay"].c_str(),"P%dY%dM%dDT%dH%dM%dS",
								&delay2.tm_year, &delay2.tm_mon, &delay2.tm_mday, &delay2.tm_hour, &delay2.tm_min, &delay2.tm_sec);
						t_delay2 = mktime(&delay2);
						if (sign == '+')
							t_delay2 += 60*off_hours+off_min;
						else
							t_delay2 -= (60*off_hours+off_min);

						if (t_start1 > t_start2 && (t_start1+t_delay1 < t_start2+t_delay2)){
							trigger_satisfied = true;
							break;
						}
					}
				}
			}

			// TriggerPersonalDataAccessedForPurpose
			if((*triggers_it)["triggerID"] == (*it)["triggerID"] && (*triggers_it)["triggerID"] == "TriggerPersonalDataAccessedForPurpose"){
				purpose_satisfied = true;
				for(unsigned int i = 0; i<arraysize(ontology_vector); i++){
					if ((*triggers_it)["Purpose"][i] >= (*it)["Purpose"][i]){
						purpose_satisfied = false;
						break;
					}
				}
				if ((*triggers_it)["MaxDelay"] >= (*it)["MaxDelay"] && purpose_satisfied == true){
					trigger_satisfied = true;
					break;
				}
			}
			
			// TriggerPersonalDataDeleted evaluation
			if((*triggers_it)["triggerID"] == (*it)["triggerID"] && (*triggers_it)["triggerID"] == "TriggerPersonalDataDeleted"){
				// Start is startNow or is the same time
				// MaxDelay from DH preference is greter or equal of application Max Delay
				if ((*triggers_it)["MaxDelay"] >= (*it)["MaxDelay"]){
					trigger_satisfied = true;
					break;
				}
			}

			// TriggerDataSubjectAccess evaluation
			if((*triggers_it)["triggerID"] == (*it)["triggerID"] && (*triggers_it)["triggerID"] == "TriggerDataSubjectAccess"){
				// Start is startNow or is the same time
				// MaxDelay from DH preference is greter or equal of application Max Delay
				if ((*triggers_it)["url"] == (*it)["url"]){
					trigger_satisfied = true;
					break;
				}
			}
		}
		if (trigger_satisfied == false)
			return false;
	}
	return true;
}
