package utils;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class Validador extends PlainDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int limite;

	public Validador(int limite) {

		this.limite = limite;
	}

	public void insertString(int ofs, String str, AttributeSet a) throws BadLocationException {

		// verifica se o que você está digitando é menor ou igual o limite que você irá
		// estabelecer. Se verdadeiro ele permitira
		// que você insira caracteres na caixa de texto
		if ((getLength() + str.length()) <= limite) {
			super.insertString(ofs, str, a);
		}

	}

}
