import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.*;

public interface EmoState extends Library  
{
	EmoState INSTANCE = (EmoState)Native.loadLibrary("edk", EmoState.class);
    
    public enum EE_EmotivSuite_t {
    	EE_COGNITIV
    }
    
	// Cognitiv action list
    public enum EE_CognitivAction_t {
   		COG_NEUTRAL		(0x0001),
   		COG_DISAPPEAR	(0x2000), // équivaut au DISAPPEAR dans l'EmoComposer
   		COG_RIGHT		(0x0040), // équivaut au LEFT dans l'EmoComposer
   		COG_LEFT		(0x0020), // équivaut au RIGHT dans l'EmoComposer
   		COG_ELSE		(0x0004); // équivaut au PULL dans l'éEmoComposer

   		private int bit;
    	EE_CognitivAction_t(int bitNumber)
    	{
    		bit = bitNumber;
    	}
    	public int ToInt()
    	{
    		return(bit);
    	}
    } 
    
	// Wireless Signal list
    public enum EE_SignalStrength_t {
    	NO_SIGNAL, BAD_SIGNAL, GOOD_SIGNAL
    } 
    
	// Liste des électrodes du casque
    public enum EE_InputChannels_t {
    	EE_CHAN_CMS, EE_CHAN_DRL, EE_CHAN_FP1, EE_CHAN_AF3, EE_CHAN_F7, 
    	EE_CHAN_F3, EE_CHAN_FC5, EE_CHAN_T7, EE_CHAN_P7, EE_CHAN_O1,
    	EE_CHAN_O2, EE_CHAN_P8, EE_CHAN_T8, EE_CHAN_FC6, EE_CHAN_F4,
    	EE_CHAN_F8, EE_CHAN_AF4, EE_CHAN_FP2
    } 
    
	// Qualité des contacts des électrodes
	public enum EE_EEG_ContactQuality_t {
		EEG_CQ_NO_SIGNAL, EEG_CQ_VERY_BAD, EEG_CQ_POOR, 
		EEG_CQ_FAIR, EEG_CQ_GOOD
	}
    
    
    // Returns the detected Cognitiv action of the user
    int ES_CognitivGetCurrentAction(Pointer state);
    
    // Returns the detected Cognitiv action power of the user
    float ES_CognitivGetCurrentActionPower(Pointer state);
    
    // Query whether the signal is too noisy for Cognitiv detection to be active
    int ES_CognitivIsActive(Pointer state);
    
    
    // Query of the current wireless signal strength
    int ES_GetWirelessSignalStatus(Pointer state);
    
    // Get the level of charge remaining in the headset battery
    void ES_GetBatteryChargeLevel(Pointer state, IntByReference chargeLevel, IntByReference maxChargeLevel);
}