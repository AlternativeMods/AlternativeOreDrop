package alternativemods.alternativeoredrop.proxy;

import alternativemods.alternativeoredrop.AlternativeOreDrop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author: Lordmau5
 * Date: 02.02.14
 * Time: 21:05
 */
public class CommonProxy {

    public void openConfigGui() {}
    public void openConfigGui(String identifiers) {}
    public void openAdjustingGui(String identifiers, Map<String, ArrayList<AlternativeOreDrop.OreRegister>> oreMapJson) {}
    public void openAdjustingOreGui(String oreName, List<AlternativeOreDrop.OreRegister> oreMapJson) {}

}