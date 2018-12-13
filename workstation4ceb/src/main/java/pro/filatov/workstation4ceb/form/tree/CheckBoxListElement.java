
package pro.filatov.workstation4ceb.form.tree;



public class CheckBoxListElement {

  private boolean selected;
  private boolean isCurrent;
  private String text;

  public CheckBoxListElement(boolean selected, String text, boolean isCurrent){
    this.selected = selected;
    this.text = text;
    this.isCurrent = isCurrent;
  }

  public boolean isSelected() {  return selected; }
  public String getText() { return text; }
  public void setSelected(boolean selected) {
    this.selected = selected;
  }
  public void setText(String text) { this.text = text; }

  public boolean isCurrent() {
    return isCurrent;
  }

  public void setCurrent(boolean current) {
    isCurrent = current;
  }
}