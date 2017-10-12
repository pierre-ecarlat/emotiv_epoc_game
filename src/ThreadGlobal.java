import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class ThreadGlobal implements Runnable
{
	private Facade fc;
	
	public ThreadGlobal(Facade fc)
	{
		this.fc = fc;
	}
	
	@SuppressWarnings("static-access")
	void defineActToTrain()
	{
		if(this.fc.currLvl == 0 && !this.fc.actTrain[1])
			this.fc.actionToTrain = 1;
		else if(this.fc.currLvl == 1 && !this.fc.actTrain[2])
			this.fc.actionToTrain = 2;
		else if(this.fc.currLvl == 2 && !this.fc.actTrain[3])
			this.fc.actionToTrain = 3;
		else if(this.fc.currLvl == 3)
			this.fc.turn = false;
		else
			this.fc.actionToTrain = -1;
	}
	
	@SuppressWarnings("static-access")
	public void run()
	{
		defineActToTrain();
		
		while(Facade.turn)
		{
			if(this.fc.haveToCheckCurrAction)
			{
				Facade.game = null;
				defineActToTrain();
				this.fc.haveToCheckCurrAction = false;
			}
			
			if(Edk.INSTANCE.EE_EngineGetNextEvent(Facade.eEvent) != EdkErrorCode.EDK_OK.ToInt())
				continue;
			
			if(Facade.actionToTrain == -1 && !Facade.gameBool)
			{
				Facade.gameBool = true;
				Facade.currLvl++;
				this.fc.createGame();
			}
			else if(Facade.actionToTrain != -1)
				Facade.trainBool = true;
			
			if((this.fc.trainBool) && (Facade.trainStep == 0))
				this.fc.Check();
			
			int eventType = Edk.INSTANCE.EE_EmoEngineEventGetType(Facade.eEvent);
			
			if((this.fc.trainBool) && (eventType == Edk.EE_Event_t.EE_CognitivEvent.ToInt()))
			{
				this.fc.game = null;
				
				int cogType = Edk.INSTANCE.EE_CognitivEventGetType(this.fc.eEvent);
				
				if(cogType == Edk.EE_CognitivEvent_t.EE_CognitivTrainingStarted.getType())
					this.fc.trainStep = 4;
				
				if(cogType == Edk.EE_CognitivEvent_t.EE_CognitivTrainingSucceeded.getType())
					Edk.INSTANCE.EE_CognitivSetTrainingControl(0,Edk.EE_CognitivTrainingControl_t.COG_ACCEPT.getType());
				
				if (cogType == Edk.EE_CognitivEvent_t.EE_CognitivTrainingFailed.getType())
					JOptionPane.showMessageDialog(new JFrame(), "Cognitiv Training Failed", "Dialog", JOptionPane.ERROR_MESSAGE);

				if (cogType == Edk.EE_CognitivEvent_t.EE_CognitivTrainingRejected.getType())
					JOptionPane.showMessageDialog(new JFrame(), "Cognitiv Training Rejected", "Dialog", JOptionPane.ERROR_MESSAGE);
				
				if(cogType == Edk.EE_CognitivEvent_t.EE_CognitivTrainingCompleted.getType())
				{
					this.fc.trainStep = 5;
					
					while(this.fc.trainStep != 6) { }

					defineActToTrain();
					
					this.fc.setStep(0);
					this.fc.trainBool = false;
					this.fc.train = null;
				}
			}
			
			if((this.fc.gameBool) && (eventType == Edk.EE_Event_t.EE_EmoStateUpdated.ToInt()))
			{
				Edk.INSTANCE.EE_EmoEngineEventGetEmoState(this.fc.eEvent, this.fc.eState);
				
				int action = EmoState.INSTANCE.ES_CognitivGetCurrentAction(this.fc.eState);
				double power = EmoState.INSTANCE.ES_CognitivGetCurrentActionPower(this.fc.eState);
				
				if(power >= 0.7)
				{
					if(action == 8192 && this.fc.actTrain[1])
						this.fc.setCurrentAction(1);
					if(action == 64 && this.fc.actTrain[2])
						this.fc.setCurrentAction(2);
					if(action == 32 && this.fc.actTrain[3])
						this.fc.setCurrentAction(3);
					if(action == 4 && this.fc.actTrain[4])
						this.fc.setCurrentAction(4);
				}
				else
					this.fc.setCurrentAction(0);
			}
		}

		Edk.INSTANCE.EE_EngineDisconnect();
    	System.out.println("Disconnected!");
    	
    	
	}
}
