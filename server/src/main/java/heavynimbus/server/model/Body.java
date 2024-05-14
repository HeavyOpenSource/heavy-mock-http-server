package heavynimbus.server.model;

import jakarta.validation.constraints.AssertTrue;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.http.HttpRequest;

@Data
@NoArgsConstructor
public class Body {
	private String content;
	private String file;

	@AssertTrue
	public boolean assertBodyIsStringOrFile() {
		return content != null ^ file != null;
	}

	public HttpRequest.BodyPublisher toBodyPublisher() {
		if (content != null) {
			return HttpRequest.BodyPublishers.ofString(content);
		}
		try {
			return HttpRequest.BodyPublishers.ofFile(new File(file).toPath());
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
