package today.opai.api.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EventChatReceived extends EventCancelable {
    private final String message;
}
