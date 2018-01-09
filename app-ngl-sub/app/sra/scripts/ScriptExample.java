package sra.scripts;


// Script interractif qui ne tourne pas pendant des heures car serveur attend reponse 
public class ScriptExample extends AbstractScript {
	
	@Override
	public void execute() {
		print("toto");
		print("titi");
		if (true) throw new RuntimeException("crash");
		print("tutu");
		print("tata");
	}
	
	@Override
	public LogLevel logLevel() {
		return LogLevel.Debug;
	}
	
}
