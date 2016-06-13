package services.print;

import java.util.LinkedList;
import java.util.List;

public abstract class BarcodePrintJob extends PrintJob {

	public BarcodePrintJob(Printer printer) {
		super(printer);
	}

	private List<String> labels;

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public List<String> getLabels() {
		List<String> orderedLabels = new LinkedList<String>();

		if (getPrintRequestAttributes().isInterleaved())
			for (int i = 0; i < labels.size(); i++)
				for (int n = 0; n < getPrintRequestAttributes().getCopies(); n++)
					orderedLabels.add(labels.get(i));
		else
			for (int n = 0; n < getPrintRequestAttributes().getCopies(); n++)
				for (int i = 0; i < labels.size(); i++)
					orderedLabels.add(labels.get(i));

		return orderedLabels;
	}
}