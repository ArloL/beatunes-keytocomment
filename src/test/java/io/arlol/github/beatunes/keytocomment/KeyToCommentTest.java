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
		song.setComments("key=4d");
		song.setKey(MajorKey.A_MAJOR);

		KeyToComment ktc = new TestableKeyToComment();
		ktc.setSong(song);

		Task task = new Task();
		task.setSong(song);

		assertThat(ktc.skip()).isFalse();
	}

}
