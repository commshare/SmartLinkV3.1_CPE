package com.alcatel.smartlinkv3.common;

public class ENUM {
	public static enum LOGIN_STATE {
		Login, Logout;

		public static LOGIN_STATE build(int nState) {
			if (nState == 0) {
				return Login;
			} else {
				return Logout;
			}
		}
	}

	public static enum OVER_TIME_STATE {
		Enable, Disable;

		public static OVER_TIME_STATE build(int nState) {
			if (nState == 1) {
				return Enable;
			} else {
				return Disable;
			}
		}
	}

	public static enum OVER_FLOW_STATE {
		Enable, Disable;

		public static OVER_FLOW_STATE build(int nState) {
			if (nState == 1) {
				return Enable;
			} else {
				return Disable;
			}
		}
	}

	/*
	 * Possible values: 0 - NoSim; 1 - PinRequired; 2 - PukRequired; 3 -
	 * Accessable (Network unlocked);//ready 4 - InvalidSim 5 - Unknown
	 */
	public static enum SIMState {
		NoSim, PinRequired, PukRequired, Accessable, InvalidSim, Unknown;

		public static SIMState build(int nState) {
			if (nState == 0) {
				return NoSim;
			} else if (nState == 1) {
				return PinRequired;
			} else if (nState == 2) {
				return PukRequired;
			} else if (nState == 3) {
				return Accessable;
			} else if (nState == 4) {
				return InvalidSim;
			} else if (nState == 5) {
				return Unknown;
			} else {
				return Unknown;
			}
		}
	}

	/*
	 * 0: Not available 1: PIN disable 2: PIN enable 3: Require PUK
	 */
	public static enum PinState {
		NotAvailable, Disable, Enable, RequirePUK;

		public static PinState build(int nState) {
			if (nState == 0) {
				return NotAvailable;
			} else if (nState == 1) {
				return Disable;
			} else if (nState == 2) {
				return Enable;
			} else if (nState == 3) {
				return RequirePUK;
			} else {
				return NotAvailable;
			}
		}
	}

	/*
	 * Possible values: 0 : Disable 1 : Enable
	 */
	public static enum AutoPinState {
		Disable, Enable;

		public static AutoPinState build(int nState) {
			if (nState == 0) {
				return Disable;
			} else {
				return Enable;
			}
		}
	}

	/*
	 * Network Type 0: No service 1: GPRS 2: EDGE 3: UMTS 4: HSDPA 5: HSPA 6:
	 * HSUPA 7: DC-HSPA+ 8: LTE 9: GSM 10: HSPA+ 11: UNKNOWN
	 */
	public static enum NetworkType {
		No_service, GPRS, EDGE, UMTS, HSDPA, HSPA, HSUPA, DC_HSPA_PLUS, LTE, GSM, HSPA_PLUS, UNKNOWN;

		public static NetworkType build(int nState) {
			if (nState == 0) {
				return No_service;
			} else if (nState == 1) {
				return GPRS;
			} else if (nState == 2) {
				return EDGE;
			} else if (nState == 3) {
				return UMTS;
			} else if (nState == 4) {
				return HSDPA;
			} else if (nState == 5) {
				return HSPA;
			} else if (nState == 6) {
				return HSUPA;
			} else if (nState == 7) {
				return DC_HSPA_PLUS;
			} else if (nState == 8) {
				return LTE;
			} else if (nState == 9) {
				return GSM;
			} else if (nState == 10) {
				return HSPA_PLUS;
			} else if (nState == 11) {
				return UNKNOWN;
			} else {
				return UNKNOWN;
			}
		}
	}

	/*
	 * RSSI value: 0: level 0 1: level 1 2: level 2 3: level 3 4: level 4 5:
	 * level 5
	 */
	public static enum SignalStrength {
		Level_0, Level_1, Level_2, Level_3, Level_4, Level_5;

		public static SignalStrength build(int nState) {
			if (nState == 0) {
				return Level_0;
			} else if (nState == 1) {
				return Level_1;
			} else if (nState == 2) {
				return Level_2;
			} else if (nState == 3) {
				return Level_3;
			} else if (nState == 4) {
				return Level_4;
			} else {
				return Level_5;
			}
		}
	}

	/*
	 * 0 : ME //M850 1 : SIM 2 : Device //H850
	 */
	public static enum SMSStoreIn {
		ME, SIM, Device;

		public static SMSStoreIn build(int nState) {
			if (nState == 0) {
				return ME;
			} else if (nState == 1) {
				return SIM;
			} else {
				return Device;
			}
		}

		public static int antiBuild(SMSStoreIn storeIn) {
			if (storeIn == ME) {
				return 0;
			} else if (storeIn == SIM) {
				return 1;
			} else {
				return 2;
			}
		}
	}

	/*
	 * 0 : read 1 : not read 2 : sent 3 : not sent 4 : report 5 : flash
	 */
	public static enum SMSTag {
		Read, NotRead, Sent, NotSent, Report, Flash;

		public static SMSTag build(int nState) {
			if (nState == 0) {
				return Read;
			} else if (nState == 1) {
				return NotRead;
			} else if (nState == 2) {
				return Sent;
			} else if (nState == 3) {
				return NotSent;
			} else if (nState == 4) {
				return Report;
			} else {
				return Flash;
			}
		}
	}

	/*
	 * 0 : none 1 : sending 2 : success 3 : fail still sending last message 4 :
	 * fail with Memory full 5 : fail
	 */
	public static enum SendStatus {
		None, Sending, Success, Fail_still_sending, Fail_Memory_Full, Fail;

		public static SendStatus build(int nState) {
			if (nState == 0) {
				return None;
			} else if (nState == 1) {
				return Sending;
			} else if (nState == 2) {
				return Success;
			} else if (nState == 3) {
				return Fail_still_sending;
			} else if (nState == 4) {
				return Fail_Memory_Full;
			} else {
				return Fail;
			}
		}
	}

	/*
	 * 0: disconnected 1: connecting 2: connected 3: disconnecting
	 */
	public static enum ConnectionStatus {
		Disconnected, Connecting, Connected, Disconnecting;

		public static ConnectionStatus build(int nState) {
			if (nState == 0) {
				return Disconnected;
			} else if (nState == 1) {
				return Connecting;
			} else if (nState == 2) {
				return Connected;
			} else if (nState == 3) {
				return Disconnecting;
			} else {
				return Disconnecting;
			}
		}
	}

	/**
	 * service type, Samba = 0; DLNA = 1; FTP = 2
	 */
	public static enum ServiceType {
		Samba, DLNA, FTP;

		public static ServiceType build(int nType) {
			if (nType == 0) {
				return Samba;
			} else if (nType == 1) {
				return DLNA;
			} else if (nType == 2) {
				return FTP;
			} else
				return Samba;
		}
	}

	/**
	 * service State, Disabled = 0; Enabled = 1
	 */
	public static enum ServiceState {
		Disabled, Enabled;

		public static ServiceState build(int nState) {
			if (nState == 0) {
				return Disabled;
			} else if (nState == 1) {
				return Enabled;
			} else
				return Disabled;
		}
	}

	/**
	 * DeviceType, MM100 = 0; USB_Device = 1
	 */
	public static enum DeviceType {
		MM100, USB_Device;

		public static DeviceType build(int nType) {
			if (nType == 0) {
				return MM100;
			} else if (nType == 1) {
				return USB_Device;
			} else
				return MM100;
		}
	}

	/**
	 * AnonymousState State, Disabled = 0; Enabled = 1
	 */
	public static enum AnonymousState {
		Disabled, Enabled;

		public static AnonymousState build(int nState) {
			if (nState == 0) {
				return Disabled;
			} else if (nState == 1) {
				return Enabled;
			} else
				return Disabled;
		}
	}

	/**
	 * AuthType, ReadOnly = 0; ReadWrite = 1
	 */
	public static enum AuthType {
		ReadOnly, ReadWrite;

		public static AuthType build(int nType) {
			if (nType == 0) {
				return ReadOnly;
			} else if (nType == 1) {
				return ReadWrite;
			} else
				return ReadOnly;
		}
	}

	/**
	 * WlanFrequency, 0:Disable; 1:2.4GHz; 2: 5GHz
	 */
	public static enum WlanFrequency {
		Disable, Frequency_24GHZ, Frequency_5GHZ;

		public static WlanFrequency build(int nType) {
			if (nType == 1) {
				return Frequency_24GHZ;
			} else if (nType == 2) {
				return Frequency_5GHZ;
			} else
				return Disable;
		}
	}

	/**
	 * SecurityMode //0: disable; 1: WEP; 2: WPA; 3: WPA2; 4: WPA/WPA2
	 */
	public static enum SecurityMode {
		Disable, WEP, WPA, WPA2, WPA_WPA2;

		public static SecurityMode build(int nType) {
			if (nType == 1) {
				return WEP;
			} else if (nType == 2) {
				return WPA;
			} else if (nType == 3) {
				return WPA2;
			} else if (nType == 4) {
				return WPA_WPA2;
			} else
				return Disable;
		}

		public static int antiBuild(SecurityMode mode) {
			if (mode == WEP) {
				return 1;
			} else if (mode == WPA) {
				return 2;
			} else if (mode == WPA2) {
				return 3;
			} else if (mode == WPA_WPA2) {
				return 4;
			} else
				return 0;
		}
	}

	/**
	 * WPAEncryption 0: TKIP 1: AES 2: AUTO
	 */
	public static enum WPAEncryption {
		TKIP, AES, AUTO;

		public static WPAEncryption build(int nType) {
			if (nType == 0) {
				return TKIP;
			} else if (nType == 1) {
				return AES;
			} else if (nType == 2) {
				return AUTO;
			} else {
				return AUTO;
			}
		}

		public static int antiBuild(WPAEncryption mode) {
			if (mode == TKIP) {
				return 0;
			} else if (mode == AES) {
				return 1;
			} else if (mode == AUTO) {
				return 2;
			} else
				return 2;
		}
	}

	/**
	 * WEPEncryption //0: OPEN 1: share
	 */
	public static enum WEPEncryption {
		Open, Share;

		public static WEPEncryption build(int nType) {
			if (nType == 0) {
				return Open;
			} else if (nType == 1) {
				return Share;
			} else {
				return Open;
			}
		}

		public static int antiBuild(WEPEncryption mode) {
			if (mode == Open) {
				return 0;
			} else if (mode == Share) {
				return 1;
			} else
				return 1;
		}
	}
	
	/**
	 * WMode 0: auto 1: 802.11a 2: 802.11b 3: 802.11g 4: 802.11a+n 5: 802.11g+n
	 */
	public static enum WModeEnum {
		WMode_auto, 
		WMode_802_11a,
		WMode_802_11b,
		WMode_802_11g,
		WMode_802_11a_n,
		WMode_802_11g_n;

		public static WModeEnum build(int nType) {
			if (nType == 0) {
				return WMode_auto;
			} else if (nType == 1) {
				return WMode_802_11a;
			} else if (nType == 2) {
				return WMode_802_11b;
			} else if (nType == 3) {
				return WMode_802_11g;
			} else if (nType == 4) {
				return WMode_802_11a_n;
			} else if (nType == 5) {
				return WMode_802_11g_n;
			} else {
				return WMode_auto;
			}
		}
	}

	public static enum WIFIConnectStatus {
		Connected, Disconnected;

		public static WIFIConnectStatus build(int nState) {
			if (nState == 0) {
				return Connected;
			} else {
				return Disconnected;
			}
		}
	}

	/*
	 * Login status 0: logout 1: some body login 2:logined
	 */
	public static enum UserLoginStatus {
		Logout, OthersLogined, selfLogined;

		public static UserLoginStatus build(int status) {
			if (status == 1) {
				return OthersLogined;
			} else if (status == 2) {
				return selfLogined;
			} else {
				return Logout;
			}
		}

		public static int antiBuild(UserLoginStatus status) {
			if (status == OthersLogined) {
				return 1;
			} else if (status == selfLogined) {
				return 2;
			} else
				return 0;
		}
	}
}
