/*******************************************************************************
 * Copyright 2014 Rafael Garcia Moreno.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.bladecoder.engineeditor.ui;

import org.w3c.dom.Element;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.bladecoder.engineeditor.model.BaseDocument;
import com.bladecoder.engineeditor.ui.components.EditElementDialog;
import com.bladecoder.engineeditor.ui.components.InputPanel;

public class EditVerbDialog extends EditElementDialog {
	public static final String VERBS[] = { "lookat", "pickup", "talkto", "use", "leave", "enter", "exit", "custom" };
	
	public static final String SCENE_VERBS[] = { "init", "test", "custom" };	
	
	public static final String VERBS_INFO[] = {
			"Called when the user clicks\n in the 'lookat' icon\n over a object in scene",
			"Called when the user clicks\n in the 'pickup' icon\n over a object in scene",
			"Called when the user clicks\n in the 'talkto' icon\n over a character in scene",
			"Called when the user drags and drops\n an inventory object over\n an object in scene or in inventory",
			"Called when the user clicks\n in an exit zone in scene",
			"Called when the player enters\n in the object bounding box",
			"Called when the player exits\n the object bounding box",
			"User defined verbs can be called\n from dialogs or inside actions using \nthe 'run_verb' action" };
	
	public static final String SCENE_VERBS_INFO[] = {
		"Called every time\n that the scene is loaded",
		"Called every time\n that the scene is loaded in test mode.\n'test' verb is called before the 'init' verb",
		"User defined verbs can be called\n from dialogs or inside actions using \nthe 'run_verb' action" };	

	
	private InputPanel[] inputs;


	String attrs[] = { "id", "state", "target"};		
	
	@SuppressWarnings("unchecked")
	public EditVerbDialog(Skin skin, BaseDocument doc, Element parentElement, Element e) {
		super(skin);
		
		inputs = new InputPanel [4];
		inputs[0] = new InputPanel(skin, "Verb ID", "Select the verb to create.", parentElement.getTagName().equals("scene")?SCENE_VERBS:VERBS);
		inputs[1] = new InputPanel(skin, "State", "Select the state.");
		inputs[2] = new InputPanel(skin, "Target BaseActor", "Select the target actor id for the 'use' verb");
		inputs[3] = new InputPanel(skin, "Custom Verb Name", "Select the Custom verb id");

		if(parentElement.getTagName().equals("scene"))
			setInfo(SCENE_VERBS_INFO[0]);
		else
			setInfo(VERBS_INFO[0]);

		((SelectBox<String>) inputs[0].getField()).addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				String id = (String) inputs[0].getText();
				int i = inputs[0].getSelectedIndex();

				if(parent.getTagName().equals("scene"))
					setInfo(SCENE_VERBS_INFO[i]);
				else
					setInfo(VERBS_INFO[i]);

				if (id.equals("use"))
					setVisible(inputs[2],true);
				else
					setVisible(inputs[2],false);

				if (id.equals("custom"))
					setVisible(inputs[3],true);
				else
					setVisible(inputs[3],false);
				
				pack();
			}

		});
		
		init(inputs, attrs, doc, parentElement, "verb", e);
		
		setVisible(inputs[2],false);
		setVisible(inputs[3],false);
		
		if(e != null) {
			boolean isCustom = true;
			String id = e.getAttribute("id");
			
			String verbs[] = parent.getTagName().equals("scene")?SCENE_VERBS:VERBS;
			
			for(String v:verbs) {
				if(v.equals(id) && !id.equals("custom")) {
					isCustom = false;
					break;
				}
			}
			
			if(isCustom) {
				inputs[0].setText("custom");
				setVisible(inputs[3],true);
				inputs[3].setText(id);
			}
			
			if(id.equals("use"))
				setVisible(inputs[2],true);
		}
	}

	@Override
	protected boolean validateFields() {
		boolean isOk = true;
		
		if(inputs[0].getText().equals("custom") && inputs[3].getText().isEmpty()) {
			inputs[3].setError(true);
			isOk = false;
		} else {
			inputs[3].setError(false);
		}
				
		return isOk;
	}
	
	@Override
	protected void fill() {
		for (int j = 0; j < a.length; j++) {
			InputPanel input = i[j];
			
			if (!input.getText().isEmpty()) {
				e.setAttribute(a[j], input.getText());
			} else {
				e.removeAttribute(a[j]);
			}
		}
		
		if(e.getAttribute("id").equals("custom"))
			e.setAttribute("id", inputs[3].getText());
	}
}
