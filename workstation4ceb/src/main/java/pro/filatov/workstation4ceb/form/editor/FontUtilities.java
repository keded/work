package pro.filatov.workstation4ceb.form.editor;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import javax.swing.UIManager;
import sun.swing.SwingLazyValue;

public class FontUtilities {

	private static Map<String, Font> originals;

	public static void setFontScale(float scale) {

		if (originals == null) {
			originals = new HashMap<>(25);
			for (Map.Entry entry : UIManager.getDefaults().entrySet()) {
				Object key = entry.getKey();
				if (key.toString().toLowerCase().contains(".font")) {
					Object value = entry.getValue();
					Font font = null;
					if (value instanceof SwingLazyValue) {
						SwingLazyValue lazy = (SwingLazyValue) entry.getValue();
						value = lazy.createValue(UIManager.getDefaults());
					}

					if (value instanceof Font) {
						font = (Font) value;
						originals.put(key.toString(), font);
					}
				}
			}
		}

		for (Map.Entry<String, Font> entry : originals.entrySet()) {
			String key = entry.getKey();
			Font font = entry.getValue();

			float size = font.getSize();
			size *= scale;

			font = font.deriveFont(Font.PLAIN, size);
			UIManager.put(key, font);
		}
	}

}