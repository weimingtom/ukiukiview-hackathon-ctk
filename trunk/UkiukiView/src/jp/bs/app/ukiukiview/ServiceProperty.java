package jp.bs.app.ukiukiview;

import java.util.List;

public class ServiceProperty {
	abstract class Component {
		boolean mandatory;
		String displayLabel;
		String paramName;
		abstract String createQueryParam();
	}

	class ListItem {
		String label;
		String param;
		boolean selected;
	}

	class CheckBox extends Component {
		List<ListItem> items;
		CheckBox(List<String> label, List<String> paramName) {

		}
		@Override
		String createQueryParam() {
			return null;
		}
	}

	class Slider {
		boolean mandatory;
		String label;
		String parameter;
		List<String> optionids;
		List<String> options;
	}
}
