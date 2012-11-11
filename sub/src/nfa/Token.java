package nfa;

public class Token {
	public String type;
	public String value;
	
	public Token(String type, String value) {
		this.type = type;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %s)",
				type == null ? "null" : type, value);
	}
	
	@Override
	public boolean equals(Object obj) {
		Token b = (Token) obj;
		return b.type.equals(this.type) && b.value.equals(this.value);
	}
}
