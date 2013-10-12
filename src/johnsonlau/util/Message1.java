package johnsonlau.util;

public class Message1 {
	private String mCmd;
	private String mValue;

	public Message1(String cmd, String value) {
		mCmd = cmd;
		mValue = value;
	}
	
	public String getCmd()
	{
		return mCmd;
	}
	
	public String getTextValue()
	{
		return mValue;
	}
}
