package serializers;

import java.io.IOException;

import com.boardgames.bastien.schotten_totten.model.Card;
import com.boardgames.bastien.schotten_totten.model.Card.COLOR;
import com.boardgames.bastien.schotten_totten.model.Card.NUMBER;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class CardDeserializer extends StdDeserializer<Card> {

	public CardDeserializer() {
		this(null);
	}

	public CardDeserializer(Class<Card> t) {
		super(t);
	}

	@Override
	public Card deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		final JsonNode node = p.getCodec().readTree(p);
		final String n = node.get("number").asText();
		final String c = node.get("color").asText();

		return new Card(NUMBER.valueOf(n), COLOR.valueOf(c));
	}

}
