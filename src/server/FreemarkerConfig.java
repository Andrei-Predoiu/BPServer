package server;

import java.io.File;
import java.io.IOException;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

public class FreemarkerConfig {
	private static Configuration cfg;

	private FreemarkerConfig() {
		cfg = new Configuration();

		// TODO Auto-generated constructor stub

		// Specify the data source where the template files come from. Here
		// I
		// set a
		// plain directory for it, but non-file-system are possible too:
		try {
			cfg.setDirectoryForTemplateLoading(new File(
					"D:/Work/git/server/MSS/WebContent/WEB-INF/templates"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Couldn't load tempaltes dir!!!!!!!");
		}

		// Specify how templates will see the data-model. This is an
		// advanced
		// topic...
		// for now just use this:
		cfg.setObjectWrapper(new DefaultObjectWrapper());

		// Set your preferred charset template files are stored in. UTF-8 is
		// a good choice in most applications:
		cfg.setDefaultEncoding("UTF-8");

		// Sets how errors will appear. Here we assume we are developing
		// HTML
		// pages.
		// For production systems TemplateExceptionHandler.RETHROW_HANDLER
		// is
		// better.
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

		// At least in new projects, specify that you want the fixes that
		// aren't
		// 100% backward compatible too (these are very low-risk changes as
		// far
		// as the
		// 1st and 2nd version number remains):
		cfg.setIncompatibleImprovements(new Version(2, 3, 20)); // FreeMarker
		// 2.3.20

	}

	private static FreemarkerConfig instance = null;

	public static Configuration getInstance() {
		if (instance == null)
			instance = new FreemarkerConfig();
		return cfg;
	}
}
