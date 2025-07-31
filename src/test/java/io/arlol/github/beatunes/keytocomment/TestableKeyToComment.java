package io.arlol.github.beatunes.keytocomment;

import com.tagtraum.audiokern.key.Key;

public class TestableKeyToComment extends KeyToComment {

	@Override
	public String toKeyString(Key key) {
		return key.getOpenKeyCode();
	}

}
