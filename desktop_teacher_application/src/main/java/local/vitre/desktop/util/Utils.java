package local.vitre.desktop.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.hashids.Hashids;
import org.json.simple.JSONArray;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.DiffFlags;
import com.flipkart.zjsonpatch.JsonDiff;
import com.flipkart.zjsonpatch.JsonPatch;

@SuppressWarnings("deprecation")
public class Utils {

	private static Random rnd = new Random();

	public static String chunkify(String str) {
		return str.trim().replaceAll("[^A-Za-z]+", "").toLowerCase();
	}

	public static String makeUsername(String fullName) {

		String firstName = getFirstName(fullName);
		String lastName = getLastName(fullName);

		String[] firstParts = firstName.split("\\s+");
		String result;

		if (firstParts.length > 1)
			result = Character.toString(firstParts[0].charAt(0)) + Character.toString(firstParts[1].charAt(0));
		else
			result = Character.toString(firstName.charAt(0)); // First char

		result += lastName;
		result += Integer.toString(rnd.nextInt(99));
		return result.toLowerCase();
	}

	public static String createUniqueHash() {
		Hashids hashids = new Hashids(time() / 2 + "", 8);
		return hashids.encode(time());
	}

	public static String createUniqueHash(int len) {
		Hashids hashids = new Hashids(time() / 2 + "", len);
		return hashids.encode(time());
	}

	public static String createUniqueHash(String salt) {
		Hashids hashids = new Hashids(salt, 8);
		return hashids.encode(time());
	}

	public static String createHumanPassword(int len) {
		if ((len % 2) != 0) { // Length paramenter must be a multiple of 2
			len = 8;
		}
		char[] conso = { 'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'r', 's', 't', 'v', 'w', 'y',
				'z' };
		char[] vowel = { 'a', 'e', 'i', 'o', 'u' };

		int length = len - 2;
		String key = "";
		int max = length / 2;

		for (int i = 1; i <= max; i++) {
			key += conso[rnd.nextInt(18)];
			key += vowel[rnd.nextInt(4)];
		}
		key += rnd.nextInt(9);
		key += rnd.nextInt(9);
		return key;
	}

	public static String createUniqueHash(String salt, int len) {
		Hashids hashids = new Hashids(salt, len);
		return hashids.encode(time());
	}

	public static long time() {
		return System.currentTimeMillis();
	}

	public static String getFirstName(String fullName) {
		String[] parts = fullName.trim().split(",");
		int cut = 0;
		
		if (parts[1].contains(".")) // FIXME Would break Ma. Lourdes Names
			cut = 2;

		String rawFirst = parts[1].substring(0, parts[1].length() - cut).trim();
		return WordUtils.capitalizeFully(rawFirst);
	}

	public static String getLastName(String fullName) {
		String[] parts = fullName.split(",");
		String rawLast = StringUtils.capitalize(parts[0]);
		return WordUtils.capitalizeFully(rawLast);
	}

	public static String namify(String str) {
		return WordUtils.capitalizeFully(str);
	}

	public static String keepTextOnly(String str) {
		return str.replaceAll("\\p{Punct}|\\d", "");
	}

	public static String keepTextOnlyNamify(String str) {
		return namify(keepTextOnly(str));
	}

	public static String getExtension(String fileName) {
		return fileName.split("\\.")[1];
	}

	public static String noZeros(String str) {
		return str.replaceAll("\\.0*$", "");
	}

	public static String noExtension(String str) {
		return str.split("\\.")[0];
	}

	public static String applyPatch(File patchFile, File applyFile) throws JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode source = mapper.readTree(applyFile);
		JsonNode patch = mapper.readTree(patchFile);
		JsonPatch.applyInPlace(patch, source);
		return source.toString();
	}

	public static String pseudoPath(String... directories) {
		String built = "";
		for (int i = 0; i < directories.length; i++) {
			String dir = directories[i];
			if (i == directories.length - 1) {
				built += dir;
				continue;
			}
			built += dir + "/";
		}

		return built;
	}

	@SuppressWarnings("unchecked")
	public static LinkedHashMap<Object, Object> createOrderedJSONMap(File file)
			throws FileNotFoundException, IOException, ParseException {

		JSONParser parser = new JSONParser();
		ContainerFactory orderedKeyFactory = new ContainerFactory() {
			public List<Object> creatArrayContainer() {
				return new ArrayList<Object>();
			}

			public Map<Object, Object> createObjectContainer() {
				return new LinkedHashMap<Object, Object>();
			}

		};

		return (LinkedHashMap<Object, Object>) parser.parse(new FileReader(file), orderedKeyFactory);

	}

	@SuppressWarnings("unchecked")
	public static LinkedHashMap<Object, Object> createOrderedJSONMap(String data)
			throws FileNotFoundException, IOException, ParseException {

		JSONParser parser = new JSONParser();
		ContainerFactory orderedKeyFactory = new ContainerFactory() {
			public List<Object> creatArrayContainer() {
				return new ArrayList<Object>();
			}

			public Map<Object, Object> createObjectContainer() {
				return new LinkedHashMap<Object, Object>();
			}

		};

		return (LinkedHashMap<Object, Object>) parser.parse(data, orderedKeyFactory);

	}

	public static JSONArray createJSONPatch(File oldPatch, File newPatch)
			throws JsonProcessingException, ParseException, IOException {
		JSONParser parser = new JSONParser();

		return (JSONArray) parser.parse(createPartialPatch(oldPatch, newPatch));
	}

	public static String createPartialPatch(File oldPatch, File newPatch) throws JsonProcessingException, IOException {
		EnumSet<DiffFlags> flags = DiffFlags.dontNormalizeOpIntoMoveAndCopy();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode source = mapper.readTree(oldPatch);
		JsonNode target = mapper.readTree(newPatch);
		JsonNode patch = JsonDiff.asJson(source, target, flags);
		return patch.toString();
	}

	public static String createPartialPatch(String oldPatch, String newPatch)
			throws JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode source = mapper.readTree(oldPatch);
		JsonNode target = mapper.readTree(newPatch);
		JsonNode patch = JsonDiff.asJson(source, target);
		return patch.toString();
	}
}
