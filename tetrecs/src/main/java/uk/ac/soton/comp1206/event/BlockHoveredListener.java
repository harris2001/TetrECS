package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlock;

public interface BlockHoveredListener {
    void hover(GameBlock block,boolean in,boolean canPlay);
}
