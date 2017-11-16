package serializers;

import java.io.IOException;

import com.boardgames.bastien.schotten_totten.model.Card;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class CardSerializer extends StdSerializer<Card> {

	public CardSerializer() {
		this(null);
	}

	public CardSerializer(Class<Card> t) {
		super(t);
	}

	@Override
	public void serialize(Card value, JsonGenerator jgen, SerializerProvider provider) 
					throws IOException, JsonProcessingException {

		jgen.writeStartObject();
		jgen.writeStringField("number", value.getNumber().name());
		jgen.writeStringField("color", value.getColor().name());
		jgen.writeEndObject();
	}

}
