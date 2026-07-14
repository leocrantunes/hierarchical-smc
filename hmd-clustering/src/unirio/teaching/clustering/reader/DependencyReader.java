package unirio.teaching.clustering.reader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import unirio.teaching.clustering.model.Project;
import unirio.teaching.clustering.model.ProjectClass;
import unirio.teaching.clustering.model.ProjectPackage;

public class DependencyReader implements ILoad
{
	private ProjectPackage getOrCreatePackage(Project project, String className)
	{
		int packageSeparatorIndex = className.lastIndexOf('.');
		String packageName = packageSeparatorIndex > 0 ? className.substring(0, packageSeparatorIndex) : "root";

		ProjectPackage projectPackage = project.getPackageName(packageName);

		if (projectPackage == null)
			projectPackage = project.addPackage(packageName);

		return projectPackage;
	}

	private void addClassIfMissing(Project project, String className)
	{
		if (project.getClassIndex(className) == -1)
		{
			ProjectPackage classPackage = getOrCreatePackage(project, className);
			project.addClass(new ProjectClass(className, classPackage));
		}
	}

	public Project load(String filename) throws FileNotFoundException
	{
		FileInputStream fis = new FileInputStream(filename);
		Scanner sc = new Scanner(fis);
		Project project = new Project("jodamoney_marlon");

		while (sc.hasNextLine())
		{
			String line = sc.nextLine().trim();

			if (line.length() > 0)
			{
				String[] parts = line.split("\\s+");

				if (parts.length >= 2)
				{
					String firstClass = parts[0];
					String secondClass = parts[1];

					addClassIfMissing(project, firstClass);
					addClassIfMissing(project, secondClass);

					ProjectClass firstProjectClass = project.getClassName(firstClass);

					if (firstProjectClass != null)
						firstProjectClass.addDependency(secondClass);
				}
			}
		}

		sc.close();
		return project;
	}
}