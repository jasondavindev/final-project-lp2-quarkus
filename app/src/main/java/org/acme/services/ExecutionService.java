package org.acme.services;

import java.io.IOException;
import java.util.Date;

import org.acme.models.Problem;
import org.acme.models.Solution;
import org.acme.utils.FileUtil;

public class ExecutionService {
	public static void executeScript(Solution solution) {
		long timestamp = new Date().getTime();
		solution.setTimestamp(timestamp);

		Problem problem = ProblemsService.findProblem(solution.getProblem());

		if (problem == null) {
			return;
		}

		String scriptFilename = String.format("%s/scripts/%s.py", ProblemsService.problemsPath, problem.getName());
		FileUtil.writeFile(scriptFilename, solution.getSourcecode());

		for (int i = 1; i <= problem.getCasesTest(); i++) {

			String inputFilename = String.format("%s/inputs/%s_%d.txt", ProblemsService.problemsPath, problem.getName(),
					i);
			String outputFilename = String.format("%s/outputs/%d_%d.txt", ProblemsService.problemsPath, timestamp, i);
			String command = String.format("python3 %s < %s > %s", scriptFilename, inputFilename, outputFilename);

			try {
				String cmd[] = { "/bin/sh", "-c", command };
				Process p = Runtime.getRuntime().exec(cmd);
				p.waitFor();
				p.destroy();
			} catch (IOException | InterruptedException e) {
				// TODO: handle exception
			}
		}
	}
}
