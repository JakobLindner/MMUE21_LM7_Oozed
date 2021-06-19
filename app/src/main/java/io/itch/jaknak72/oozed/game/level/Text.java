package io.itch.jaknak72.oozed.game.level;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import io.itch.jaknak72.oozed.game.ObjectFactories;
import io.itch.jaknak72.oozed.game.objects.GameObject;

/**
 * Ingame-Text
 * @author simon
 */
public class Text extends Tile{
    private static final String TEXT_KEY = "text";
    private static final String NAME_KEY = "name";

    public String text;
    public String name;

    public Text() {}
    public Text(Text other) {
        super(other);
        this.text = other.text;
    }

    @Override
    public void build(GameObject root, Context context) {
        if(text==null || text.isEmpty()) {
            text = "NO TEXT";
            if(name!=null && !name.isEmpty()) {
                int id = context.getResources().getIdentifier(name,"string",context.getPackageName());
                if(id!=0)
                    text = context.getString(id);
            }
        }
        root.addChild(ObjectFactories.makeText(x,y,text));
    }

    @Override
    public void fromJSON(JSONObject json) throws JSONException {
        super.fromJSON(json);
        if(json.has(TEXT_KEY))
            this.text = json.getString(TEXT_KEY);
        if(json.has(NAME_KEY))
            this.name = json.getString(NAME_KEY);

    }
}
