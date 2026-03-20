//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        SkiArea skiArea = new SkiArea();
        Skier skier = new Skier();

        CommandProcessor processor = new CommandProcessor(skiArea, skier);
        processor.run();
    }
}