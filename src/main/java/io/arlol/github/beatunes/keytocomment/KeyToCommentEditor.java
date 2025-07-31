/*
 * =================================================
 * Copyright 2014 tagtraum industries incorporated
 * All rights reserved.
 * =================================================
 */
package io.arlol.github.beatunes.keytocomment;

import java.awt.*;
import java.util.prefs.Preferences;

import javax.swing.*;

import com.tagtraum.beatunes.BeaTunes;
import com.tagtraum.beatunes.KeyTextRenderer;
import com.tagtraum.beatunes.analysis.TaskEditor;

/**
 * Configuration editor for {@link KeyToComment} task.
 *
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 */
public class KeyToCommentEditor implements TaskEditor<KeyToComment> {

	private static final Preferences PREFERENCES = java.util.prefs.Preferences
			.userNodeForPackage(KeyToCommentEditor.class);
	private static final String ANALYSIS_OPTIONS_KEY_RENDERER = "analysisoptions.key.renderer";

	private BeaTunes application;
	private final JPanel component = new JPanel();
	private final JComboBox<KeyTextRenderer> keyTextRendererComboBox = new JComboBox<>();
	private final JLabel keyFormatLabel = new JLabel("Key Format:");

	public KeyToCommentEditor() {
		this.component.setLayout(new BorderLayout());
		this.component.setOpaque(false);
		this.component.add(keyFormatLabel, BorderLayout.WEST);
		this.component.add(keyTextRendererComboBox, BorderLayout.CENTER);
	}

	@Override
	public void setApplication(final BeaTunes beaTunes) {
		this.application = beaTunes;
	}

	@Override
	public BeaTunes getApplication() {
		return application;
	}

	@Override
	public void init() {
		// this localization key happens to be defined in beaTunes 4.0.4 and
		// later
		this.keyFormatLabel.setText(application.localize("Key_Format"));
		final java.util.List<KeyTextRenderer> renderers = application
				.getPluginManager()
				.getImplementations(KeyTextRenderer.class);
		this.keyTextRendererComboBox.setModel(
				new DefaultComboBoxModel<>(
						renderers.toArray(new KeyTextRenderer[0])
				)
		);
		this.keyTextRendererComboBox.setSelectedItem(
				application.getGeneralPreferences().getKeyTextRenderer()
		);
		this.keyTextRendererComboBox.setOpaque(false);
		this.keyTextRendererComboBox.setRenderer(new DefaultListCellRenderer() {

			@Override
			public Component getListCellRendererComponent(
					final JList<?> list,
					final Object value,
					final int index,
					final boolean isSelected,
					final boolean cellHasFocus
			) {
				final String s;
				if (value instanceof KeyTextRenderer) {
					s = ((KeyTextRenderer) value).getName();
				} else {
					s = "";
				}
				return super.getListCellRendererComponent(
						list,
						s,
						index,
						isSelected,
						cellHasFocus
				);
			}

		});

		final String className = PREFERENCES.get(
				ANALYSIS_OPTIONS_KEY_RENDERER,
				application.getGeneralPreferences()
						.getKeyTextRenderer()
						.getClass()
						.getName()
		);
		for (final KeyTextRenderer renderer : renderers) {
			final String rendererClassName = KeyToComment
					.getClassName(renderer);
			if (rendererClassName.equals(className)) {
				keyTextRendererComboBox.setSelectedItem(renderer);
				break;
			}
		}
		this.component.addPropertyChangeListener("enabled", evt -> {
			final Boolean enabled = (Boolean) evt.getNewValue();
			keyTextRendererComboBox.setEnabled(enabled);
			keyFormatLabel.setEnabled(enabled);
		});
	}

	@Override
	public JComponent getComponent() {
		return component;
	}

	@Override
	public void setTask(final KeyToComment keyToComment) {
		final String rendererClass = keyToComment.getRendererClass();
		for (int i = 0; i < keyTextRendererComboBox.getItemCount(); i++) {
			final KeyTextRenderer renderer = keyTextRendererComboBox
					.getItemAt(i);
			if (KeyToComment.getClassName(renderer).equals(rendererClass)) {
				keyTextRendererComboBox.setSelectedIndex(i);
				break;
			}
		}
	}

	@Override
	public KeyToComment getTask(final KeyToComment keyToComment) {
		final KeyTextRenderer renderer = keyTextRendererComboBox
				.getItemAt(keyTextRendererComboBox.getSelectedIndex());
		final String className = KeyToComment.getClassName(renderer);
		keyToComment.setRendererClass(className);
		PREFERENCES.put(ANALYSIS_OPTIONS_KEY_RENDERER, className);
		return keyToComment;
	}

	@Override
	public KeyToComment getTask() {
		final KeyToComment keyToComment = new KeyToComment();
		return getTask(keyToComment);
	}

}
