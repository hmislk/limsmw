package org.openhealth.limsmw;

/**
 *
 * @author Buddhika
 */
public enum Analyzer {
    BioSystemBa400,
    SysMex;
    
    public String getLabel(){
        String label="";
        switch (this) {
            case BioSystemBa400:
                label = "Bio Systems BA 400";
                break;
            case SysMex:
                label = "Sysmex";
                break;
            default:
                label = this.toString();
        }
        return label;
    }
}
