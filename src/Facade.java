import com.sun.jna.Pointer;

public class Facade
{
	public static int[] cognitivActionList = // Liste des actions possibles
		{  EmoState.EE_CognitivAction_t.COG_NEUTRAL.ToInt(),
		   EmoState.EE_CognitivAction_t.COG_DISAPPEAR.ToInt(),
		   EmoState.EE_CognitivAction_t.COG_RIGHT.ToInt(),
		   EmoState.EE_CognitivAction_t.COG_LEFT.ToInt(),
		   EmoState.EE_CognitivAction_t.COG_ELSE.ToInt()
		};
	
	public static Pointer eEvent		= Edk.INSTANCE.EE_EmoEngineEventCreate();
	public static Pointer eState		= Edk.INSTANCE.EE_EmoStateCreate();
	public static short composerPort	= 1726;
	public static int option 			= 2;
	
	public static int nbAction			= cognitivActionList.length;
	public static Boolean[] actTrain	= new Boolean[nbAction];
	
	public static Boolean gameBool		= false;
	public static Boolean trainBool		= false;
	public static int actionToTrain		= 0;
	public static int trainStep			= 0;
	public static int currAction		= -1;
	public static int currLvl			= 0;
	
	public Boolean haveToCheckCurrAction = false;
	
	public static Thread global;
	public static Thread train;
	public static Thread game;
	
	public static Boolean turn = true;

	public Facade()
	{
		Edk.INSTANCE.EE_EmoEngineEventFree(eEvent);
		
		global = new Thread(new ThreadGlobal(this));
    	
		actTrain[0] = true;
		for(int i = 1 ; i < nbAction ; i++)
			actTrain[i] = false;

		switch (option)
    	{
    		case 1: // On travaille avec le casque
    		{
    			if (Edk.INSTANCE.EE_EngineConnect("Emotiv Systems-5") != EdkErrorCode.EDK_OK.ToInt()) // Le casque ne se connecte pas, défaut hardware
    			{
    				System.out.println("Emotiv Engine start up failed."); // Pour autant, pas de print avec option = 1 et pas de casque
    				return;
    			}
    			break;
    		}
    		case 2: // On travaille avec EmoComposer
    		{	
    			if (Edk.INSTANCE.EE_EngineRemoteConnect("127.0.0.1", composerPort, "Emotiv Systems-5") != EdkErrorCode.EDK_OK.ToInt()) // N'arrive pas à se connecter à emocomposer
    			{
    				System.out.println("Cannot connect to EmoComposer on [127.0.0.1]");
    				//JOptionPane jop = new JOptionPane();
    				return;
    			}
    			break;
    		}
    		default: // Autre cas
    		{
    			System.out.println("Invalid option...");
    			return;
    		}
    	}
		
		global.start();
	}
    
	public void setCurrentAction(int value)
	{
		currAction = value;
	}
	
	public String currentActionToString(int value)
	{
		if(value == 0)
			return "NEUTRAL";
		if(value == 1)
			return "DISAPPEAR";
		if(value == 2)
			return "RIGHT";
		if(value == 3)
			return "LEFT";
		if(value == 4)
			return "ELSE";
		
		return "UNKNOWN";
	}
    
	public void setStep(int value)
	{
		trainStep = value;
	}
	
	public void createGame()
	{
		if(game == null)
		{
			game = new Thread(new ThreadGame(this));
			game.start();
		}
	}
	
    public void Check()
    {
		trainStep = 1;
		
		train = new Thread(new ThreadTrain(this));
		train.start();
		
		while(trainStep != 2) {	}
		
		if(actionToTrain == 0) // Neutral sélectionné
		{
			Edk.INSTANCE.EE_CognitivSetTrainingAction(0, EmoState.EE_CognitivAction_t.COG_NEUTRAL.ToInt()); // 0 : userID et l'action dont on veut définir le type
			Edk.INSTANCE.EE_CognitivSetTrainingControl(0, Edk.EE_CognitivTrainingControl_t.COG_START.getType()); // Idem avec ce qu'on veut faire de l'action (start, reset, ...)
		}
		
		if(actionToTrain == 1) // Jump sélectionné
		{
			try
			{
				EnableCognitivAction(EmoState.EE_CognitivAction_t.COG_DISAPPEAR, true); // On met "true" dans la case du tableau correspondante
				EnableCognitivActionsList(); // Définit s'il existe au moins l'une des actions en true, les actions courrantes actives
				StartTrainingCognitiv(EmoState.EE_CognitivAction_t.COG_DISAPPEAR);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		if(actionToTrain == 2) // Left sélectionné, idem que pour Push
		{
			try
			{
				EnableCognitivAction(EmoState.EE_CognitivAction_t.COG_RIGHT, true);
				EnableCognitivActionsList();
				StartTrainingCognitiv(EmoState.EE_CognitivAction_t.COG_RIGHT);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		if(actionToTrain == 3) // Right sélectionné, idem que pour Push
		{
			try
			{
				EnableCognitivAction(EmoState.EE_CognitivAction_t.COG_LEFT, true);
				EnableCognitivActionsList();
				StartTrainingCognitiv(EmoState.EE_CognitivAction_t.COG_LEFT);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		if(actionToTrain == 4) // Else sélectionné, idem que pour Push
		{
			try
			{
				EnableCognitivAction(EmoState.EE_CognitivAction_t.COG_ELSE, true);
				EnableCognitivActionsList();
				StartTrainingCognitiv(EmoState.EE_CognitivAction_t.COG_ELSE);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		trainStep = 3;
    }
	
    public static void StartTrainingCognitiv(EmoState.EE_CognitivAction_t cognitivAction)
    {
        if (cognitivAction == EmoState.EE_CognitivAction_t.COG_NEUTRAL) // N'arrive jamais
        {
        	Edk.INSTANCE.EE_CognitivSetTrainingAction(0,EmoState.EE_CognitivAction_t.COG_NEUTRAL.ToInt());
			Edk.INSTANCE.EE_CognitivSetTrainingControl(0, Edk.EE_CognitivTrainingControl_t.COG_START.getType());
        }
        else
            for (int i = 1 ; i < nbAction ; i++)
                if (cognitivAction.ToInt() == cognitivActionList[i]) // On checke si on est dans la bonne action, et si elle est autorisée
                    if (actTrain[i]) // Pour l'utilisateur 0, on définit un start sur l'action
                    {
                    	Edk.INSTANCE.EE_CognitivSetTrainingAction(0, cognitivAction.ToInt());
                    	Edk.INSTANCE.EE_CognitivSetTrainingControl(0, Edk.EE_CognitivTrainingControl_t.COG_START.getType());
                    }
    }
    
    public static void EnableCognitivAction(EmoState.EE_CognitivAction_t cognitivAction, Boolean iBool)
    {
        for (int i = 1 ; i < cognitivActionList.length ; i++)
            if (cognitivAction.ToInt() == cognitivActionList[i])
            	actTrain[i] = iBool;
    }
    
    public static void EnableCognitivActionsList()
    {
        long cognitivActions = 0x0000;
        
        for (int i = 1 ; i < cognitivActionList.length ; i++)
            if (actTrain[i])
                cognitivActions = cognitivActions | ((long)cognitivActionList[i]);
        
        Edk.INSTANCE.EE_CognitivSetActiveActions(0, cognitivActions);
    }
}