package main;

public class VoiceType {
	private String voice;
	private String type;
	
	public VoiceType(String v, String t) {
		voice = v;
		type = t;
	}
	
	public String getType() {
		return type;
	}
	
	public String getVoice() {
		return voice;
	}
}
