package local.vitre.desktop.record;

public class Elo {

	// highest elo would be ~3,107
	
	public static final double ELO_CONSTRAINT = 2.1;

	public static double maxElo(GradingComponent comp) {
		return Math.ceil((comp.getMaxItemCount() * comp.getWeight().doubleValue()) / ELO_CONSTRAINT);
	}

	public static double highestPossibleElo(GradingComponent comp) {
		return Math.ceil((comp.getItemCount() * comp.getWeight().doubleValue()) / ELO_CONSTRAINT);
	}

	public static double calculate(double score, double scoreCap, double weight) {
		return Math.ceil((weight * ((score / scoreCap) * 100)) / ELO_CONSTRAINT);
	}
}
