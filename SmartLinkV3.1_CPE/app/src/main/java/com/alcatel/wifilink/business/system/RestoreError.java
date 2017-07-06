package com.alcatel.wifilink.business.system;

import com.alcatel.wifilink.business.BaseResult;
import com.alcatel.wifilink.common.ENUM.EnumRestoreErrorStatus;

public class RestoreError extends BaseResult {

	private int RestoreErr=EnumRestoreErrorStatus.
			antiBuild(EnumRestoreErrorStatus.RESTORE_ERROR_OTHER_ERROR);
	@Override
	protected void clear() {
		// TODO Auto-generated method stub

		RestoreErr = EnumRestoreErrorStatus.
				antiBuild(EnumRestoreErrorStatus.RESTORE_ERROR_OTHER_ERROR);
	}
	
	public int getRestoreError() {
		return RestoreErr;
	}
	public void setRestoreError(int nRestoreError) {
		RestoreErr = nRestoreError;
	}

}
