package ea.example.showcase;

import ea.edu.Kreis;
import ea.edu.Spiel;
import ea.edu.event.BildAktualisierungReagierbar;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DelayedRegistration {
    public static void main(String[] args) {
        new PONG();
    }

    static class PONG extends SPIEL {
        Kreis kreis;

        public PONG() {
            super();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            this.kreis = new Kreis(1);
        }

        @Override
        public void bildAktualisierungReagieren(double verganeneSekunden) {
            kreis.verschieben(.1, 0);
        }
    }

    static abstract class SPIEL extends Spiel implements BildAktualisierungReagierbar {
        public SPIEL() {
            super();

            setzeSchwerkraft(0);

            Class<?> currentClass = getClass();
            Set<String> classNames = new HashSet<>();

            do {
                classNames.add(currentClass.getName());
                currentClass = currentClass.getSuperclass();
            } while (currentClass != null);

            Thread thread = Thread.currentThread();
            parallel(() -> {
                boolean withinConstructor = true;

                while (withinConstructor) {
                    withinConstructor = Arrays.stream(thread.getStackTrace()).anyMatch(element -> element.getMethodName().equals("<init>") && classNames.contains(element.getClassName()));
                }

                registriereBildAktualisierungReagierbar(this);
            });
        }
    }
}
