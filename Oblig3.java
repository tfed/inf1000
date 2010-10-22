/* Obligatorisk oppgave 3
 *
 * Ettersom jeg var litt usikker paa om jeg kunne bruke engelske navn paa
 * klassene valgte jeg norsk, mens resten skal vaere paa engelsk.
 *
 * Har brukt litt av skjelettet som ble gitt som prekode. Av annen kode har jeg
 * kun brukt snutter jeg har skrevet selv, enten til denne oppgaven eller
 * tidligere.
 *
 * Provde aa kommentere der jeg folte det var nodvendig, men det meste i denne
 * koden boer vaere mulig aa forstaa uten forklaring.
 *
 * - Thomas Emil Westbye Fedog
 *
 **/


import java.util.Scanner;
import java.io.*;

class Oblig3 {
    public static void main(String[] args) {
        Utsyn s = new Utsyn();
        s.start();
    }
}

class Student {
    String name;
    int balance;
}

class Hybel {
    Student tenant;
    int rent;
    int floor;
    char room;
}

class Utsyn {
    Scanner in = new Scanner(System.in);

    int totFloors = 3;
    int totRooms = 6;
    Hybel[][] appartment = new Hybel[totFloors][totRooms];

    /* Building data */
    int month;
    int year;
    int totalIncome;
    int totalMonths;

    final String FILNAVN = "hybeldata.txt";
    final String TOM_HYBEL = "TOM HYBEL";


    Utsyn() {
        File filename = new File(FILNAVN);
        try {
            Scanner scan = new Scanner(filename);

            /* Fetching building data from the first line */
            String firstline = scan.nextLine();
            String[] data = firstline.split(";");

            /* Since we're fetching the text file as strings we have to parse
             * them to integers (and characters for the appartment) where
             * necessary */
            month = Integer.parseInt(data[0]);
            year = Integer.parseInt(data[1]);
            totalIncome = Integer.parseInt(data[2]);
            totalMonths = Integer.parseInt(data[3]);

            /* Variables for appartment and tenant data */
            int floor;
            char room;
            int balance;
            String name;

            /* Fetching the remaining file with current tenants and their data*/
            while (scan.hasNextLine()) {
                String input = scan.nextLine();
                String[] option = input.split(";");

                /*  -  */
                floor = Integer.parseInt(option[0]);
                room = option[1].charAt(0);
                balance = Integer.parseInt(option[2]);
                name = option[3];

                /* Lowering floor by 1 to avoid array exception */
                floor -= 1;

                /* Appartment data */
                int roomNum = toInt(room);
                appartment[floor][roomNum] = new Hybel();
                appartment[floor][roomNum].floor = floor+1;
                appartment[floor][roomNum].room = room;

                /* Setting the rent for penthouse appartments and others */
                if (floor == 2) {
                    appartment[floor][roomNum].rent = 6000;
                } else {
                    appartment[floor][roomNum].rent = 5000;
                }

                /* Tenant data */
                appartment[floor][roomNum].tenant = new Student();
                appartment[floor][roomNum].tenant.name = name;
                appartment[floor][roomNum].tenant.balance = balance;
            } /* End while loop */
        } catch (FileNotFoundException ex) {
            System.err.println("error: file not found");
        }
    }

    /* Our method for the command line menu */
    void start() {
        Scanner input = new Scanner(System.in);

        int command = -1;
        while (command != 0) {
            System.out.println("Meny:");
            System.out.println("1. Skriv ut oversikt");
            System.out.println("2. Registrer ny leietager");
            System.out.println("3. Registrer betaling ");
            System.out.println("4. Registrer utflytting");
            System.out.println("5. Oppdater data ved ny maned");
            System.out.println("6. Tilkall torpedo for tvangsutkastelse");
            System.out.println("0. Avslutt");

            System.out.print("Hva vil du gjore? ");
            command = Integer.parseInt(input.nextLine());
            System.out.println(" ");
            switch(command) {
                case 1: getOverview(); break;
                case 2: regNewTenant(); break;
                case 3: regPayment(); break;
                case 4: regMoving(); break;
                case 5: monthlyUpdate(); break;
                case 6: eviction(); break;
                case 0: shutdown(); break;
                default: System.out.println("Choose from 0-6"); break;
            }
        }
    }


    /* Simple method to print out an overview of the appartment building to the
     * terminal */
    void getOverview() {
        Hybel app;

        /* My feeble attempt of trying to format a nice looking overview */
        System.out.printf("%s %8s %15s", "Hybel ", "Leietager", "Saldo");
        System.out.println(" ");
        System.out.println("----- ------------------- -------");
        for (int i = 0; i < totFloors; i++) {
            for (int j = 0; j < totRooms; j++) {
                app = findApp(i,j);

                System.out.printf("%2d%c", app.floor, app.room);
                if (app.tenant.name.equals("TOM HYBEL") && app.tenant.balance == 0) {
                    System.out.printf("%17s", "( LEDIG )");
                } else {
                    System.out.printf("%17s", app.tenant.name);
                }
                System.out.printf("%13d", app.tenant.balance);
                System.out.println(" ");
            }
        }
        System.out.println("---------------------------------------------");
        System.out.println(" Maned/Ar, og driftstid: " + month + "/" + year + ", " + totalMonths + " i drift");
        System.out.println(" Totalfortjeneste: " + totalIncome);
        System.out.println("---------------------------------------------");

    }

    /* Method for adding a new tenant to the building.
     *
     * Won't do anything if all appartments are taken */
    void regNewTenant() {
        /* Variable to check for free appartments */
        int antFreeApp = 0;

        for (int i = 0; i < totFloors; i++) {
            for (int j = 0; j < totRooms; j++) {
                if (checkIfEmpty(i, j)) {
                    antFreeApp += 1;
                }
            }
        }
        /* If full, end it.
         * else add the tenant data */
        if (antFreeApp == 0) {
            System.out.println("Det er dessverre ingen ledige hybler.");
        } else {
            Hybel app;
            System.out.println("Folgende hybler er ledige:");
            for (int i = 0; i < totFloors; i++) {
                for (int j = 0; j < totRooms; j++) {
                    if (checkIfEmpty(i, j)) {
                        app = findApp(i, j);
                        System.out.println(app.floor + "" + app.room);
                    }
                }
            }
            System.out.print("Hvilken vil du ha? ");
            Scanner in = new Scanner(System.in);
            String tmp = in.nextLine();

            int tmpFloor = getFloor(tmp);
            int tmpRoom = getRoom(tmp);

            app = findApp(tmpFloor, tmpRoom);
            System.out.print("Navn: ");
            String name = in.nextLine();
            System.out.println(" ");
            app.tenant.name = name;
            app.tenant.balance = 10000;
            if (tmpFloor == 2) {
                app.rent = 6000;
                app.tenant.balance -= app.rent;
                totalIncome += app.rent;
            } else {
                app.rent = 5000;
                app.tenant.balance -= app.rent;
                totalIncome += app.rent;
            }
            System.out.println(name + " har na flyttet inn i " + app.floor + "" + app.room);
            System.out.println("Gjenvaerende saldo: "+ app.tenant.balance);
            System.out.println(" ");
        }
    }

    /* Method for updating the tenants account */
    void regPayment() {
        Hybel app;

        System.out.print("Hvilken hybel? ");
        Scanner in = new Scanner(System.in);
        String tmp = in.nextLine();
        System.out.println(" ");

        if (checkIfEmpty(getFloor(tmp), getRoom(tmp)))  {
            System.out.println("Det bor ingen her.");

        } else {
            app = findApp(getFloor(tmp), getRoom(tmp));

            System.out.print("Hvor mye vil du sette inn pa kontoen?");
            int updateBalance = Integer.parseInt(in.nextLine());
            System.out.println(" ");

            app.tenant.balance += updateBalance;

            System.out.println("Kontoen har blitt oppdatert.");
            System.out.println("Gjenvaerende saldo: "+ app.tenant.balance);
            System.out.println(" ");
        }
    }

    /* Method for a tenant moving out.
     *
     * Will check if tenant exists, if so - remove name, pay fee and close
     * account */
    void regMoving() {
        Hybel app;

        int fee = 650;
        System.out.println("Hvilken student skal flytte ut?");
        for (int i = 0; i < totFloors; i++) {
            for (int j = 0; j < totRooms; j++) {
                app = findApp(i,j);
                if (checkIfEmpty(i, j)) {
                    System.out.print("");
                } else {
                    System.out.println("- " + app.tenant.name);
                }
            }
        }

        System.out.print("Navn: ");
        Scanner in = new Scanner(System.in);
        String name = in.nextLine();

        app = findAppByName(name);
        if (app != null) {
            if (app.tenant.balance < fee) {
                app.tenant.balance -= fee;
                int rest = app.tenant.balance * (-1);
                totalIncome += rest;
                app.tenant.balance = 0;
                System.out.println(app.tenant.name + " matte betale " + rest + "kr i rest");
                app.tenant.name = "TOM HYBEL";
            } else {
                app.tenant.balance -= fee;
                totalIncome += fee;
                app.tenant.balance = 0;
                System.out.println(app.tenant.name + " har na flyttet ut.");
                app.tenant.name = "TOM HYBEL";
            }
        } else {
            System.out.println("Det er ingen ved navn " + name + " som bor her");
        }

    }

    /* Method for monthly fees and other general data */
    void monthlyUpdate() {
        System.out.print("Er du sikker pa at idag er manedens forste dag? (J j / N n) ");
        Scanner in = new Scanner(System.in);
        String answer = in.nextLine();
        System.out.println(" ");
        if (answer.equals("j") || answer.equals("J")) {
            Hybel app;
            int monthlyIncome = 0;
            /* If month 12, happy new year! */
            if (month == 12) {
                month = 1;
                year += 1;
            } else {
                month += 1;
            }
            totalMonths += 1;

            /* Maintenance for every appartment and every floor */
            int maintenanceApp = 1200;
            int maintenanceFloor = 1700;
            for (int i = 0; i < totFloors; i++) {
                for (int j = 0; j < totRooms; j++) {
                    app = findApp(i, j);
                    /* If statements for checking if appartments are empty, or
                     * the tenants balance is less then the rent. Else just
                     * taking rent (5000 if on floor1,2 and 6000 if on 3) from
                     * the tenancts accounts.*/
                    if (app.tenant.name.equals("TOM HYBEL")) {
                        monthlyIncome -= maintenanceApp;
                    } else if (app.tenant.balance <= 0) {
                        app.tenant.balance -= app.rent;
                        monthlyIncome -= maintenanceApp;
                    } else if (app.tenant.balance < app.rent) {
                        int rest = 0;
                        monthlyIncome += app.tenant.balance;
                        rest = app.rent - app.tenant.balance;
                        app.tenant.balance -= rest;
                        monthlyIncome -= maintenanceApp;
                    } else {
                        monthlyIncome += app.rent;
                        monthlyIncome -= maintenanceApp;
                        app.tenant.balance -= app.rent;
                    }
                }
                monthlyIncome -= maintenanceFloor;
            }
            totalIncome += monthlyIncome;

            System.out.println("Maned/ar, og driftstid: " + month + "/" + year + ", " + totalMonths + " i drift");
            System.out.println("Manedens fortjeneste: " + monthlyIncome + "kr");
            System.out.println("Totalfortjeneste: " + totalIncome + "kr");
            System.out.println("Gjnmst. manedlig fortjeneste: " + totalIncome / totalMonths);
            System.out.println(" ");
        } else {
            System.out.println("J j = Oppdater data ved manedsskift");
            System.out.println("Alt annet = Avbryt");
        }
    }

    /* Method for checking if tenants are over the maximum limit of the
     * appartments rent * (-1). If so, we call the man. */
    void eviction() {
        Hybel app;
        int totEvicted = 0;
        for (int i = 0; i < totFloors; i++) {
            for (int j = 0; j < totRooms; j++) {
                app = findApp(i, j);
                if (app.tenant.balance < app.rent * (-1)) {
                    callTheMan(app.floor - 1, toInt(app.room), app.tenant.balance * (-1));
                    totEvicted += 1;
                }
            }
        }
        if (totEvicted == 0) {
            System.out.println("Ingen ligger over maksimumsgrensen");
        }
    }

    /* Specialist in "removing" tenants with no money */
    void callTheMan(int floor, int room, int amount) {
        Hybel app;
        /* The eviction fee is 3000, but Gulbrand and the specialist splits the
         * money half/half. */
        int fee = 1500;
        app = findApp(floor, room);

        totalIncome += amount + fee;

        System.out.println(app.tenant.name + " har blitt kastet ut av hybel " + app.floor + "" + app.room);
        System.out.println("Gulbrand inkasserer " + amount + "kr (rest) + "+ fee + "kr (gebyr)");
        app.tenant.balance  = 0;
        app.tenant.name = "TOM HYBEL";

        System.out.println(" ");
    }

    /* Method for closing the program.
     *
     * Will write existing data to the file we took it from */
    void shutdown() {
        Hybel app;
        PrintWriter out;
        File filename = new File(FILNAVN);
        try {
            out = new PrintWriter(filename);

            out.println(month + ";" + year + ";" + totalIncome + ";" + totalMonths);
            for (int i = 0; i < totFloors; i++) {
                for (int j = 0; j < totRooms; j++) {
                    app = findApp(i, j);
                    out.println(app.floor + ";" + app.room + ";" + app.tenant.balance + ";" + app.tenant.name);
                }
            }
            System.out.println("Data har blitt skrevet til fil: " + FILNAVN);
            System.out.println("Ha en flott dag.");
            out.close();
        } catch (FileNotFoundException e) {
            System.err.println(
            "Det skjedde en feil ved apning av filen "
            + filename.getAbsolutePath());
            e.printStackTrace();
        }
    }



    /* Helper method for returning an appartment object with floor and room
     * number as the data provided. */
    Hybel findApp(int floor, int room) {
        return appartment[floor][room];
    }

    /* Helper method for returning an appartment object with the tenants as only
     * data provided */
    Hybel findAppByName(String s) {
        Hybel app;
        for (int i = 0; i < totFloors; i++) {
            for (int j = 0; j < totRooms; j++) {
                app = findApp(i,j);
                if(app.tenant.name.equals(s)) {
                    return app;
                }
            }
        }
        return null;
    }

    /* Helper method for converting char to int.
     *
     * Used when reading from command line. Can't use the toInt(char c); method
     * as char '1' != int 1*/
    public int getFloor(String s) {
        return Character.digit(s.charAt(0), 10) - 1;
    }

    /* Simple method to failsafe the program if someone use lowercase
     * characters for the room numbers. Will also return the char value as 
     * int. */
    public int getRoom(String s) {
        char c = s.charAt(1);
        if (Character.isUpperCase(c)) {
            return toInt(c);
        } else {
            char ch = Character.toUpperCase(c);
            return toInt(ch);
        }
    }

    /* Helper method for checking if an appartment is empty.*/
    public boolean checkIfEmpty(int floor, int room) {
        Hybel app = findApp(floor, room);
        if (app.tenant.name.equals("TOM HYBEL") && app.tenant.balance == 0) {
            return true;
        } else {
            return false;
        }
    }

    /* Simple char to int conversion */
    public int toInt(char c) {
        return (int) (c - 'A');
    }

    /* Simple int to char conversion */
    public char toChar(int i) {
        return (char) (i + 'A');
    }

}
