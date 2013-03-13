package org.webinos.api.mediacontent;

/**
 * Description: Defines orientation of a image. The meaning of available values is illustrated, for example, at http://www.imagemagick.org/Usage/warping/.
 * 
 * Values: NORMAL, FLIP_HORIZONTAL, ROTATE_180, FLIP_VERTICAL, TRANSPOSE, ROTATE_90, TRANSVERSE, ROTATE_270.
 * 
 * @author marius
 * 
 */
public enum MediaImageOrientation {
	NORMAL(0), FLIP_HORIZONTAL(1), ROTATE_180(2),
	FLIP_VERTICAL(3), TRANSPOSE(4), ROTATE_90(5),
	TRANSVERSE(6), ROTATE_270(7), UNDEFINED(8);

	int intValue;

	private MediaImageOrientation(int intVal) {
		this.intValue = intVal;
	}

	public int toInteger() {
		// TODO Auto-generated method stub
		switch (this) {
		case NORMAL:
			return 0;
		case FLIP_HORIZONTAL:
			return 1;
		case ROTATE_180:
			return 2;
		case FLIP_VERTICAL:
			return 3;
		case TRANSPOSE:
			return 4;
		case ROTATE_90:
			return 5;
		case TRANSVERSE:
			return 6;
		case ROTATE_270:
			return 7;
		case UNDEFINED:
			return 8;
		default:
			throw new IllegalStateException("Not a predefined value!");
		}
	}

	@Override
	public String toString() {
		switch (this) {
		case NORMAL:
			return "NORMAL";
		case FLIP_HORIZONTAL:
			return "FLIP_HORIZONTAL";
		case ROTATE_180:
			return "ROTATE_180";
		case FLIP_VERTICAL:
			return "FLIP_VERTICAL";
		case TRANSPOSE:
			return "TRANSPOSE";
		case ROTATE_90:
			return "ROTATE_90";
		case TRANSVERSE:
			return "TRAVERSE";
		case ROTATE_270:
			return "ROTATE_270";
		case UNDEFINED:
			return "UNDEFINED";
		default:
			throw new IllegalStateException("Not a predefined value");
		}
	}

}
