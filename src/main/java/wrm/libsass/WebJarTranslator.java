package wrm.libsass;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import org.webjars.WebJarAssetLocator;

class WebJarTranslator {

	Optional<URI> translate(URI uri) {


		ArrayList<String> nameAndPartialpathArrayList = new ArrayList<>(Arrays.asList(uri.toString().split("/")));
		
		String partialPath = "";
		String name = "";
		String fullPath = null;
		
		if (!nameAndPartialpathArrayList.isEmpty()) {
			name = nameAndPartialpathArrayList.get(0);
			
			//remove the name from the array list
			nameAndPartialpathArrayList.remove(0);
			partialPath = nameAndPartialpathArrayList.stream()
				      .map(n -> String.valueOf(n))
				      .collect(Collectors.joining("/"));
			WebJarAssetLocator webJarAssetLocator = new WebJarAssetLocator();
			fullPath = webJarAssetLocator.getFullPathExact(name, partialPath);
		}
		
		return fullPath == null ? Optional.empty() : Optional.of(URI.create(fullPath));
	}
}