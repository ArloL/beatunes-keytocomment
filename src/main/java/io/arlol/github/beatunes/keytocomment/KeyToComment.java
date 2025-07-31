/*
 * =================================================
 * Copyright 2009 tagtraum industries incorporated
 * All rights reserved.
 * =================================================
 */
package io.arlol.github.beatunes.keytocomment;

import java.util.List;

import javax.persistence.Entity;

import org.jruby.RubyObject;
import org.python.core.PyProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tagtraum.audiokern.key.Key;
import com.tagtraum.beatunes.KeyTextRenderer;
import com.tagtraum.beatunes.analysis.AnalysisException;
import com.tagtraum.beatunes.analysis.SongAnalysisTask;
import com.tagtraum.beatunes.analysis.Task;
import com.tagtraum.beatunes.keyrenderer.DefaultKeyTextRenderer;

/**
 * Copies tonal key info to the comments field using the configured renderer.
 * Note that this functionality is already built into beaTunes (starting with
 * <a href=
 * "http://blog.beatunes.com/2015/08/looking-good-beatunes-45.html">version
 * 4.5</a>). This plugin therefore only serves demo purposes.
 *
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 */

// ============================================================================== //
// It is *essential* for this class to be annotated as Entity.                    //
// Otherwise it will not be saved in the analysis queue and cannot be processed.  //
// ============================================================================== //
@Entity
public class KeyToComment extends SongAnalysisTask {

	private static final Logger LOG = LoggerFactory
			.getLogger(KeyToComment.class);

	public KeyToComment() {
		// this task does not take long - therefore we ignore it in per task
		// progress bars
		setProgressRelevant(false);
	}

	public void setRendererClass(final String klass) {
		setProperty("renderer", klass);
	}

	public String getRendererClass() {
		final String renderer = getProperty("renderer");
		return renderer == null ? DefaultKeyTextRenderer.class.getName()
				: renderer;
	}

	public KeyTextRenderer getRenderer() {
		final String desiredRenderer = getRendererClass();
		final List<KeyTextRenderer> renderers = getApplication()
				.getPluginManager()
				.getImplementations(KeyTextRenderer.class);
		for (final KeyTextRenderer renderer : renderers) {
			final String rendererClass = getClassName(renderer);
			if (rendererClass.equals(desiredRenderer))
				return renderer;
		}
		// default to DefaultKeyTextRenderer
		return getApplication().getPluginManager()
				.getImplementation(DefaultKeyTextRenderer.class);
	}

	public String toKeyString(Key key) {
		// during testing we cant set the application so we need a fallback
		if (getApplication() == null) {
			return key.getOpenKeyCode();
		}
		return getRenderer().toKeyString(key);
	}

	/**
	 * Returns a verbose description of the task in HTML format. This is shown
	 * in the Analysis Options dialog (left pane).
	 *
	 * @return verbose HTML description.
	 */
	@Override
	public String getDescription() {
		return "<h1>Key To Comment</h1><p>Copies the tonal key (if it exists) to the comment field using the configured format.</p>";
	}

	/**
	 * This will be the displayed name of the analysis task.
	 *
	 * @return HTML string
	 */
	@Override
	public String getName() {
		return "<html>Copy key to<br>comment field</html>";
	}

	/**
	 * This is where the actual work occurs. This method is called by beaTunes
	 * when this task is processed in the analysis/task queue.
	 *
	 * @throws AnalysisException if something goes wrong.
	 */
	@Override
	public void runBefore(final Task task) throws AnalysisException {
		if (skip()) {
			LOG.debug("Skipped {}", getSong());
			return;
		}
		final String newComment = getNewComments();
		LOG.debug("Setting comment to: {}", newComment);
		getSong().setComments(newComment);
	}

	/**
	 * Indicates, whether this task can be skipped.
	 *
	 * @return true or false
	 */
	@Override
	public boolean skip() {
		final String comments = getSong().getComments();
		if (comments == null) {
			return false;
		}
		if (comments.contains("mixed")) {
			return true;
		}
		if (comments.contains("ignore")) {
			return true;
		}
		return comments.startsWith(getNewComments());
	}

	private String getNewComments() {
		return "Key " + toKeyString(getSong().getKey());
	}

	/**
	 * Ruby and Python object's classnames are not the same after the JVM
	 * exists. Therefore we have to get their type's name, which is persistent.
	 *
	 * @param renderer renderer
	 * @return classname
	 */
	public static String getClassName(final KeyTextRenderer renderer) {
		final String classname;
		if (renderer instanceof RubyObject) {
			classname = "__jruby."
					+ ((RubyObject) renderer).getMetaClass().getName();
		} else if (renderer instanceof PyProxy) {
			classname = "__jython."
					+ ((PyProxy) renderer)._getPyInstance().getType().getName();
		} else {
			classname = renderer.getClass().getName();
		}
		return classname;
	}

}
