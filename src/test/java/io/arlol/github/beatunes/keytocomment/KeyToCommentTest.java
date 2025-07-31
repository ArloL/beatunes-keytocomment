package io.arlol.github.beatunes.keytocomment;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.tagtraum.audiokern.key.MajorKey;
import com.tagtraum.beatunes.analysis.Task;
import com.tagtraum.beatunes.library.Song;

public class KeyToCommentTest {

	@Test
	public void testCanBeSkipped() throws Exception {
		Song song = new Song();
		song.setComments("Key 4d");
		song.setKey(MajorKey.A_MAJOR);

		KeyToComment ktc = new KeyToComment();
		ktc.setSong(song);

		Task task = new Task();
		task.setSong(song);

		assertThat(ktc.skip()).isTrue();
	}

}
