import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.*;
import com.sun.jna.Structure;

public interface Edk extends Library  
{
    	Edk INSTANCE = (Edk)Native.loadLibrary("edk", Edk.class);
    	
    	// Cognitiv Suite training control enumerator
    	public enum EE_CognitivTrainingControl_t {
    		COG_NONE(0), COG_START(1), COG_ACCEPT(2), COG_REJECT(3), COG_ERASE(4), COG_RESET(5);
    		
    		private int type;
    		EE_CognitivTrainingControl_t(int val)
    		{
    			type = val;
    		}
    		public int getType()
    		{
    			return type;
    		}
    	}

    	// EmoEngine event types
    	public enum EE_Event_t {
    		EE_UnknownEvent			 (0x0000),
    		EE_EmulatorError		 (0x0001),
    		EE_ReservedEvent		 (0x0002),
    		EE_UserAdded			 (0x0010),
    		EE_UserRemoved			 (0x0020),
    		EE_EmoStateUpdated		 (0x0040),
    		EE_ProfileEvent			 (0x0080),
    		EE_CognitivEvent		 (0x0100),
    		EE_ExpressivEvent		 (0x0200),
    		EE_InternalStateChanged  (0x0400),
    		EE_AllEvent				 (0x07F0);

    		private int bit;
    		EE_Event_t(int bitNumber)
    		{
    			bit = bitNumber;
    		}
    		public int ToInt()
    		{
    			return(bit);
    		}
    	} 

    	// Expressiv-specific event types
    	public enum EE_ExpressivEvent_t {
    		EE_ExpressivNoEvent, EE_ExpressivTrainingStarted, EE_ExpressivTrainingSucceeded,
    		EE_ExpressivTrainingFailed, EE_ExpressivTrainingCompleted, EE_ExpressivTrainingDataErased,
    		EE_ExpressivTrainingRejected, EE_ExpressivTrainingReset
    	} 
    	
    	// Cognitiv-specific event types
    	public enum EE_CognitivEvent_t {
    		EE_CognitivNoEvent (0), 
    		EE_CognitivTrainingStarted (1), 
    		EE_CognitivTrainingSucceeded (2),
    		EE_CognitivTrainingFailed(3), 
    		EE_CognitivTrainingCompleted(4),
    		EE_CognitivTrainingDataErased(5),
    		EE_CognitivTrainingRejected(6), 
    		EE_CognitivTrainingReset(7),
    		EE_CognitivAutoSamplingNeutralCompleted(8), 
    		EE_CognitivSignatureUpdated(9);
    		
    		private int cType;
    		EE_CognitivEvent_t(int val)
    		{
    			cType = val;
    		}
    		public int getType()
    		{
    			return cType;
    		}
    	}

    	// Input sensor description
    	public static class InputSensorDescriptor_t extends Structure {
    		EmoState.EE_InputChannels_t		channelId;  // logical channel id
    		int								fExists;    // does this sensor exist on this headset model
    		String							pszLabel;   // text label identifying this sensor
    		double							xLoc;       // x coordinate from center of head towards nose
    		double							yLoc;       // y coordinate from center of head towards ears
    		double							zLoc;       // z coordinate from center of head toward top of skull
    	}


    	// Initializes the connection to EmoEngine
    	int EE_EngineConnect(String strDevID);
    	// Initializes the connection to a remote instance of EmoEngine
    	int EE_EngineRemoteConnect(String szHost, short port, String strDevID);
    	// Terminates the connection to EmoEngine
    	int EE_EngineDisconnect();

    	// Returns a handle to memory that can hold an EmoEngine event
    	Pointer EE_EmoEngineEventCreate();
    	// Returns a handle to memory that can hold a profile byte stream
    	Pointer EE_ProfileEventCreate();
    	//! Frees memory referenced by an event handle
    	void EE_EmoEngineEventFree(Pointer hEvent);
    	
    	// Returns a handle to memory that can store an EmoState
    	Pointer EE_EmoStateCreate();
    	// Frees memory referenced by an EmoState handle
    	void EE_EmoStateFree(Pointer hState);


    	// Returns the event type for an event already retrieved using EE_EngineGetNextEvent
    	int EE_EmoEngineEventGetType(Pointer hEvent);
    	// Returns the Cognitiv-specific event type for an EE_CognitivEvent event already retrieved using EE_EngineGetNextEvent
    	int EE_CognitivEventGetType(Pointer hEvent);

    	
    	// Copies an EmoState returned with a EE_EmoStateUpdate event to memory referenced by an Pointer
    	int EE_EmoEngineEventGetEmoState(Pointer hEvent, Pointer hEmoState);
    	

    	// Retrieves the next EmoEngine event
    	int EE_EngineGetNextEvent(Pointer hEvent);
    	// Clear a specific EmoEngine event type or all events currently inside the event queue
    	int EE_EngineClearEventQueue(int eventTypes);

    	
    	// Retrieves number of active users connected to the EmoEngine
    	int EE_EngineGetNumUser(IntByReference pNumUserOut);
    	// Sets the player number displayed on the physical input device (currently the USB Dongle) that corresponds to the specified user
    	int EE_SetHardwarePlayerDisplay(int userId, int playerNum);


    	// Set the current Cognitiv active action types
    	int	EE_CognitivSetActiveActions(int userId, long activeActions);
    	// Get the current Cognitiv active action types
    	int EE_CognitivGetActiveActions(int userId, NativeLongByReference pActiveActionsOut);

    	
    	// Return the duration of a Cognitiv training session
    	int EE_CognitivGetTrainingTime(int userId, IntByReference pTrainingTimeOut);
    	// Set the training control flag for Cognitiv training
    	int EE_CognitivSetTrainingControl(int userId, int control);
    	// Set the type of Cognitiv action to be trained
    	int EE_CognitivSetTrainingAction(int userId, int action);
    	// Get the type of Cognitiv action currently selected for training
    	int EE_CognitivGetTrainingAction(int userId, IntByReference pActionOut);
    	// Gets a list of the Cognitiv actions that have been trained by the user
        int EE_CognitivGetTrainedSignatureActions(int userId, NativeLongByReference pTrainedActionsOut);
    	// Gets the current overall skill rating of the user in Cognitiv
        int EE_CognitivGetOverallSkillRating(int userId, FloatByReference pOverallSkillRatingOut);
    	// Gets the current skill rating for particular Cognitiv actions of the user
        int EE_CognitivGetActionSkillRating(int userId, int action, FloatByReference pActionSkillRatingOut);
    	// Set the overall sensitivity for all Cognitiv actions
    	int EE_CognitivSetActivationLevel(int userId, int level);

    	// Set the sensitivity of Cognitiv actions (1 to 10)
    	int EE_CognitivSetActionSensitivity(int userId,	int action1Sensitivity, int action2Sensitivity,
    													int action3Sensitivity, int action4Sensitivity);
    	// Get the overall sensitivity for all Cognitiv actions
    	int EE_CognitivGetActivationLevel(int userId, IntByReference pLevelOut);
    	// Query the sensitivity of Cognitiv actions
    	int EE_CognitivGetActionSensitivity(int userId, IntByReference pAction1SensitivityOut, IntByReference pAction2SensitivityOut,
    													IntByReference pAction3SensitivityOut, IntByReference pAction4SensitivityOut);

    	
    	// Start the sampling of Neutral state in Cognitiv
    	int EE_CognitivStartSamplingNeutral(int userId);
    	// Stop the sampling of Neutral state in Cognitiv
    	int EE_CognitivStopSamplingNeutral(int userId);

    	// Returns a struct containing details about the specified EEG channel's headset 
    	int EE_HeadsetGetSensorDetails(int channelId, InputSensorDescriptor_t pDescriptorOut);
    	// Returns the current hardware version of the headset and dongle for a particular user
    	int EE_HardwareGetVersion(int userId, NativeLongByReference pHwVersionOut);
    	// Returns the current version of the Emotiv SDK software
    	int EE_SoftwareGetVersion(String pszVersionOut, int nVersionChars, NativeLongByReference pBuildNumOut);
    	// Returns the delta of the movement of the gyro since the previous call for a particular user
    	int EE_HeadsetGetGyroDelta(int userId, IntByReference pXOut, IntByReference pYOut);
    	// Re-zero the gyro for a particular user
    	int EE_HeadsetGyroRezero(int userId);
}
    