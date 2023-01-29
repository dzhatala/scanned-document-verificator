package dnsmatch;

public interface FieldValueExtractor {
	/**
	 * return the value of the specific field
	 * @param field
	 * @return
	 * @throws Exception 
	 */
	Object extractValue(String field) throws Exception;
}
