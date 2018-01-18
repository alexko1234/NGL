package sra.scripts;


// Script interractif qui ne tourne pas pendant des heures car serveur attend reponse 
public class ScriptExample extends AbstractScript {
	
	@Override
	public void execute() {
		println("toto");
		println("titi");
		if (true) throw new RuntimeException("crash");
		println("tutu");
		println("tata");
	}
	
	@Override
	public LogLevel logLevel() {
		return LogLevel.Debug;
	}
	
}
