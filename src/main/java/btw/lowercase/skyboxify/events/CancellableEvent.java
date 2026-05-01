package btw.lowercase.skyboxify.events;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class CancellableEvent implements Event {
    private boolean cancelled = false;
}