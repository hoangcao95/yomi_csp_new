package vn.yotel.vbilling.thread;

import vn.yotel.commons.exception.AppException;
import vn.yotel.thread.ManageableThread;

public class ProcessCleanLog extends ManageableThread {

	@Override
	protected boolean processSession() throws AppException {
		return false;
	}

}
