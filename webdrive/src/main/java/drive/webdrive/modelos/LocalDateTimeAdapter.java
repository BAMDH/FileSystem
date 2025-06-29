package drive.webdrive.modelos;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private static final DateTimeFormatter formateador = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public void write(JsonWriter out, LocalDateTime valor) throws IOException {
        if (valor == null) {
            out.nullValue();
        } else {
            out.value(valor.format(formateador));
        }
    }
    
    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String fechaString = in.nextString();
        return LocalDateTime.parse(fechaString, formateador);
    }
}