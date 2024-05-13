package com.example.lapa4;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

//import static com.sun.org.apache.xml.internal.serializer.utils.Utils.messages;


public class Listener {
    BufferedReader reader;
    BufferedWriter writer;
    public ServerSocket serverSocket;
    Socket client;
    private String currentUsername;
    private String currentFolder;

    public Listener() throws IOException {
        serverSocket = new ServerSocket(143);
    }

    public void initListener(ServerSocket serverSocket) throws IOException {
        try {
            client = serverSocket.accept();
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() throws IOException {
        if (serverSocket!=null) {
            while (true) {
                initListener(serverSocket);
                new Thread(()->{
                try {
                    wr("*", "OK details");
                    currentUsername = null;
                    IMAPMessage message = new IMAPMessage();
                    boolean isUserAuthenticated = false;
                    while (true) {
                        String line = rd();
                        if (line == null)
                            continue;
                        String[] tokens = line.split(" ");
                        boolean requiresAuthorization = false;
                        processImapMessage(tokens);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                }).start();
            }
        }
    }

    private void processImapMessage(String[] tokens) {
        try {
            switch (tokens[1]) {
                case "CAPABILITY" -> {
                    wr("*", "CAPABILITY IMAP4rev1 NAMESPACE UNSELECT CHILDREN SPECIAL-USE LIST-EXTENDED LIST-STATUS");
                    wr(tokens[0], "OK CAPABILITY completed");
                }
                case "NOOP" -> wr(tokens[0], "OK NOOP completed");
                case "LOGIN" -> loginResponse(tokens[0], tokens[2], tokens[3]);

            /*case "AUTHENTICATE":
                //  AuthenticateResponse(tokens[0], tokens[2], tokens[3], "");//trb sa iau emailul de undeva dar din cate vad nu apare in mesajele anterioare
                break;
            */
                case "NAMESPACE" -> namespaceResponse(tokens[0]);
                case "LIST" -> listResponse(tokens[0], tokens[2], tokens[3]);
                case "EXAMINE", "SELECT" -> examineResponse(tokens[0], tokens[2]);
                case "FETCH" -> {

                    ArrayList<String> options = new ArrayList<>();
                    int offset = 3;
                    for (int i = offset; i < tokens.length; i++) {
                        if (tokens[i].indexOf('(')>=0 || tokens[i].indexOf(')')>=0){
                            tokens[i] = tokens[i].replace("(","");
                            tokens[i] = tokens[i].replace(")","");
                        }
                        options.add(tokens[i]);
                    }
                    fetchResponse(tokens[0], options);
                }
                case "UID" -> {
                    ArrayList<String> options = new ArrayList<>();
                    int offset = 3;
                    for (int i = offset; i < tokens.length; i++) {
                        if (tokens[i].indexOf('(')>=0 || tokens[i].indexOf(')')>=0){
                            tokens[i] = tokens[i].replace("(","");
                            tokens[i] = tokens[i].replace(")","");
                        }
                        options.add(tokens[i]);
                    }
                    if (tokens[2].equalsIgnoreCase("fetch")){
                        UIDFetchResponse(tokens[0], tokens[3], options);}
                    else if (tokens[2].equalsIgnoreCase("store")){
                        UIDStoreResponse(tokens[0], tokens[3], options);
                    }
                }
                case "UNSELECT" -> {
                    wr(tokens[0], "OK return to authentificated state. Succes");
                    currentFolder = "";
                }
                case "SEARCH" -> searchResponce(tokens[0]);
                case "LOGOUT"-> logoutResponce(tokens[0]);
                default -> wr("*", "BAD");
            }
        }catch (IndexOutOfBoundsException e){
            System.out.println("error");
        }
    }

    private void UIDStoreResponse(String tag, String UID, ArrayList<String> flagsPart) {
        try {

            //StringBuilder UIDResponse = new StringBuilder("1 FETCH (UID " + UID);
            Path mail = Paths.get("./",currentUsername, currentFolder, UID);

            String messagePath = String.valueOf(mail);
            Session session = Session.getInstance(System.getProperties());

            // Открываем файл с использованием FileInputStream
            try (FileInputStream fis = new FileInputStream(messagePath)) {
                // Создаем объект MimeMessage из InputStream
                MimeMessage message = new MimeMessage(session, fis);
                if (flagsPart.contains("+FLAGS")) {
                    // Извлечение флагов, которые нужно добавить
                    Flags newFlags = new Flags();
                    if (flagsPart.contains("\\Seen")) {
                        newFlags.add(Flags.Flag.SEEN);
                    }
                    // Другие флаги можно добавлять здесь

                    // Установка новых флагов
                    message.setFlags(newFlags, true);
                }
            }catch (IOException e){
                wr(tag, "BAD");
            }

            wr(tag, "OK UID STORE completed");
            //wr(tag, "OK success");

        } catch (Exception e) {
            System.out.println("A14 NO UID not found");
            e.printStackTrace();
        }
    }

    private void logoutResponce(String tag) {
        wr(tag, "OK LOGOUT completed");
    }

    private void searchResponce(String tag) {
        StringBuilder searchResponce = new StringBuilder("SEARCH ");
        Path currPath = Path.of("./"+currentUsername+"/"+currentFolder);
        try {
            long fileCount = Files.list(currPath).filter(Files::isRegularFile).count();
            for (int i = 0; i < fileCount; i++) {
                searchResponce.append(i+1).append(" ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        wr("*", searchResponce.toString());
        wr(tag, "OK SEARCH completed");
    }

    private void fetchResponse(String tag, ArrayList<String> options) {
        String inboxDirectoryPath = String.valueOf(Paths.get(currentUsername));
        inboxDirectoryPath +="/"+ Paths.get(currentFolder);
        File inboxDirectory = new File(inboxDirectoryPath);
        File[] mails = inboxDirectory.listFiles();
        StringBuilder fetchResponse;
        int counter = 1;
        if (mails != null) {
            for (File mail : mails) {
                String messagePath = String.valueOf(Paths.get(mail.getPath()));
                Session session = Session.getInstance(System.getProperties());

                // Открываем файл с использованием FileInputStream
                try (FileInputStream fis = new FileInputStream(messagePath)) {
                    // Создаем объект MimeMessage из InputStream
                    MimeMessage message = new MimeMessage(session, fis);
                    //MimeMessage message = new MimeMessage()

                    String localUID = mail.getName();
                    int dotPos = mail.getName().indexOf('.');
                    if (dotPos != -1)
                        localUID = mail.getName().substring(0, dotPos);

                    fetchResponse = new StringBuilder(counter + " FETCH (");

                    for (String option : options) {
                        if (option.startsWith("BODY.PEEK")) {
                            option = "HEADERS";
                        }
                        switch (option) {
                            case "INTERNALDATE" -> {
                                String internaldate = getInternaldateForMail(message);
                                fetchResponse.append(internaldate);
                            }
                            case "UID" -> {
                                fetchResponse.append("UID ").append(localUID);
                            }
                            //тут можно через getFlags
                            case "FLAGS" -> {
                                fetchResponse.append(" FLAGS (").append(message.getFlags()).append(")");
                            }
                            case "RFC822.SIZE" -> {
                                fetchResponse.append(" RFC822.SIZE ").append(message.getSize());
                            }
                            case "HEADERS" -> {
                                String request = " BODY.PEEK[HEADER.FIELDS (DATE FROM SUBJECT CONTENT-TYPE X-MS-TNEF-Correlator CONTENT-CLASS IMPORTANCE PRIORITY X-PRIORITY THREAD-TOPIC REPLY-TO)]";
                                String headers = getHeadersForMail(message);
                                fetchResponse.append(request);
                                fetchResponse.append(headers);
                            }
                            case "BODYSTRUCTURE" -> {
                                String bodyStructure = getBodyStructureForMail(message);
                                fetchResponse.append(bodyStructure);
                            }
                            case "ENVELOPE" -> {
                                String envelope = getEnvelopeForMessage(message);
                                fetchResponse.append(envelope);
                            }
                        }

                }
                fetchResponse.append(")");

                    wr("*", fetchResponse.toString());
                    counter++;
                } catch (MessagingException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
            wr(tag, "OK FETCH completed");
        }
    }

    private String getHeadersForMail(MimeMessage message) throws MessagingException {

        String[] heads = {"DATE", "FROM" , "SUBJECT", "CONTENT-TYPE", "X-MS-TNEF-Correlator", "CONTENT-CLASS", "IMPORTANCE",
                "PRIORITY", "X-PRIORITY", "THREAD-TOPIC", "REPLY-TO"};

        Map<String, String> headers = new LinkedHashMap<>();
        int headerByteCount = 0;
        for (String header : heads) {
            String[] values = message.getHeader(header);
            String headerValue = "";
            if (values != null) {
                 headerValue = String.join(", ", values);
                headers.put(header, headerValue);
                } else {
                headers.put(header, "NIL");
            }
            headerByteCount += header.length(); // Подсчет байтов
            headerByteCount += headerValue.length();
            headerByteCount+=": ".length();// Подсчет байтов

        }
        StringBuilder response = new StringBuilder();
        headerByteCount+=headers.entrySet().size()*3;

        response.append(" {").append(headerByteCount).append("}").append("\n");

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            response.append(entry.getKey()).append(": ").append(entry.getValue());
            response.append("\n");
        }
        return response.append("\n").toString();
    }

    private String getInternaldateForMail(MimeMessage message) throws MessagingException {
        String result = "INTERNALDATE ";
        if (message.getReceivedDate()!=null) {
            result += "\""+message.getReceivedDate().toString() + "\"";
        } else {
            result += "NIL ";
        }
        return result;
    }

    private static String getAddressStructure(InternetAddress address) {
        String result = "(";
        result += "\"" + (address.getPersonal() != null ? address.getPersonal() : "NIL") + "\""; // name

        // Check for route, JavaMail does not have a specific 'route' concept, but address route might be implied
        String route = "NIL";
        result += " " + route;

        String mailbox = address.getAddress();
        String mailboxName = mailbox.split("@")[0]; // Before '@'
        String mailboxHost = mailbox.split("@")[1]; // After '@'

        result += String.format(" \"%s\" \"%s\"", mailboxName, mailboxHost);

        result += ")";
        return result;
    }
    private static String getAddresses(Address[] addresses) {
        if (addresses == null || addresses.length == 0) {
            return "NIL";
        }

        return Arrays.stream(addresses)
                .filter(Objects::nonNull)
                .map(address -> {
                    if (address instanceof InternetAddress) {
                        return getAddressStructure((InternetAddress) address);
                    } else {
                        return "NIL";
                    }
                })
                .collect(Collectors.joining("", "(", ")"));
    }
    private String getEnvelopeForMessage(MimeMessage message) throws MessagingException {
        //FROM      (("Razvan O." NIL "ogreanrazvan" "gmail.com"))

        String from = getAddresses(message.getFrom());

//SENDER
        String sender = getAddresses(new Address[] {message.getSender()});

//reply-to
        String replyTo = getAddresses(message.getReplyTo());


//to
        String to = getAddresses(message.getRecipients(Message.RecipientType.TO));

//cc
        String cc = getAddresses(message.getRecipients(Message.RecipientType.CC));

//bcc
        String bcc = getAddresses(message.getRecipients(Message.RecipientType.BCC));

//in-reply-to
        String inReplyTo = message.getHeader("In-Reply-To", null) != null ? message.getHeader("In-Reply-To", null) : "NIL";

//messageID
        String messageId = message.getMessageID();
        // Envelope structure
        String result = "ENVELOPE (";
        result += String.format(
                "\"%s\" \"%s\" %s %s %s %s %s %s %s \"<%s>\"",
                message.getSentDate() != null ? message.getSentDate().toString() : "NIL",
                message.getSubject() != null ? message.getSubject() : "NIL",
                from, sender, replyTo, to, cc, bcc, inReplyTo,
                messageId != null ? messageId : "NIL"
        );
        result += ")";

        return result;
    }

    private void UIDFetchResponse (String tag, String UID, ArrayList<String> options){
        //Path inboxDirectoryPath = Paths.get(currentUsername);
        StringBuilder UIDResponse = new StringBuilder("1 FETCH (UID " + UID+" ");
        Path mail = Paths.get("./",currentUsername, currentFolder, UID);

        String messagePath = String.valueOf(mail);
        Session session = Session.getInstance(System.getProperties());

        // Открываем файл с использованием FileInputStream
        try (FileInputStream fis = new FileInputStream(messagePath)) {
            // Создаем объект MimeMessage из InputStream
            MimeMessage message = new MimeMessage(session, fis);
            for (String option : options) {

                if (option.equalsIgnoreCase("RFC822.HEADER")) {
                    UIDResponse.append("RFC822.HEADER");
                    UIDResponse.append(getHeadersForMail(message));
                }
                else if (option.startsWith("BODY.PEEK")) {
                    UIDResponse.append(option);
                    UIDResponse.append(getBodyForMail(message));
                }
            }
            UIDResponse.append(")");
            wr(tag, UIDResponse.toString());
            wr(tag, "OK UID FETCH completed");
        } catch (IOException | MessagingException e) {
            System.out.println(e.getMessage());
            //throw new RuntimeException(e);
            wr(tag, "BAD");
        }
    }

    private String getBodyForMail(MimeMessage message) {
        StringBuilder response = new StringBuilder();
        int bodyByteCount=0;
        Object content;
        try {
            content = message.getContent();

            if (content instanceof Multipart multipart) {
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    if (bodyPart.isMimeType("text/plain")) {
                        response.append( (String) bodyPart.getContent()); // Возвращаем текстовую часть
                    } else if (bodyPart.isMimeType("text/html")) {
                        response.append ((String) bodyPart.getContent()); // Или HTML часть
                    }
                    bodyByteCount+=bodyPart.getSize();
                }
            }
            response.insert(0, " {"+(bodyByteCount)+"}\n");
        } catch(IOException | MessagingException e){
            throw new RuntimeException(e);
        }
        return response.toString();
    }

    private void examineResponse (String tag, String folder){

            folder = folder.replace("\"", "");
            currentFolder = folder;
            String inboxDirectory = currentUsername + "/" + currentFolder;

            List<File> mails = getMails(inboxDirectory);

            //mails = inboxDirectory.getFileSystem();

            int ExistsNo = mails.size();
            int UIDnext = 0;
            for (File mail : mails) {
                String localUID = mail.getName();
                int dotPos = mail.getName().indexOf('.');
                if (dotPos != -1)
                    localUID = mail.getName().substring(0, dotPos);

                if (Integer.parseInt(localUID) > UIDnext)
                    UIDnext = Integer.parseInt(localUID);
            }

            wr("*", "FLAGS (\\Answered \\Flagged \\Draft \\Deleted \\Seen)");
            wr("*", "OK [PERMANENTFLAGS ()] No permanent flags permitted.");
            wr("*", "OK [UIDVALIDITY 7] UIDs valid.");      //un numar folosit la sincronizarea UID-urilor dintre client si server (daca nu se schimba de la un EXAMINE la altul -> sicnronizarea e okay)
            wr("*", ExistsNo + " EXISTS");            //cate mailuri sunt in acest mailbox
            wr("*", "0 RECENT");            //cate au aparut de la ultima interogare EXAMINE
//wr("*", "OK[UNSEEN 1] Message 1 is first unseen");  // asta daca exista mesaje RECENTE!!! -> UID-ul la primul in acest caz
            wr("*", "OK [UIDNEXT " + UIDnext + "] Predicted next UID.");       // UID pt ultimul mail + 1
            wr("*", "OK [HIGHESTMODSEQ 245306]");
            wr(tag, "OK [READ-ONLY] "+ currentFolder +" selected. (Success)");

        }

        private List<File> getMails (String inboxDirectory){
            List<File> mails = new ArrayList<>();

            File directory = new File("./"+inboxDirectory);

            if (!directory.isDirectory()) {
                System.out.println("Path is not a directory");
            }

            // Получаем массив файлов в директории
            File[] filesArray = directory.listFiles();
            if (filesArray != null) {
                for (File file : filesArray) {
                    if (file.isFile()) { // Убедимся, что это файл, а не директория
                        mails.add(file);
                    }
                }
            }
            return mails;
        }

        private void listResponse (String tag, String reference, String mailbox){
            //daca ajugnem aici, inseamna ca e un client logat
//ne intereseaza sa ii listam folderele la care are acces acel client
//reference + mailbox -> calea in care cautam folderele (relativ la ce stie clientul, nu la PC-ul serverului)


            reference = reference.replace("\"", "");
            if (!reference.isEmpty()) {
                wr("*", "LIST () \"/\" ");
                //wr("*", "LIST (\\HasNoChildren \\Sent \\Subscribed) \"/\" ");
            } else
                wr("*", "LIST () \"/\" \"\"");

            wr(tag, "OK LIST completed");

        }

        private void namespaceResponse (String tag){
            // personal namespace: INBOX cu delimitatorul "." (se acceseaza foldere: INBOX.sent)
//NIL NIL -> pentru other's namespace si shared namespec (care nu le configuram)
            wr("*", "NAMESPACE ((\"\" \"/\")) NIL  NIL");
//wr("*", "NAMESPACE ((\"\" \"/\")) NIL  NIL");
            wr(tag, "OK NAMESPACE completed");

        }

        private void loginResponse (String tag, String username, String password){
            if (checkCredentials(username, password)) {
                wr(tag, "OK LOGIN completed");
                currentUsername = username.replace("\"", "");
                ;
            } else {
                wr(tag, "NO Logon failure: unknown user name or bad password");
            }
        }

        private boolean checkCredentials (String username, String password){
            String[] credentials;
            ArrayList<String> users = new ArrayList<>();
            try {
                BufferedReader fileReader = new BufferedReader(new FileReader(IMAPServer.fileCredentials));
                String line = fileReader.readLine();
                while (line != null) {
                    users.add(line);
                    line = fileReader.readLine();
                }
                for (String user : users) {
                    credentials = user.split(" ");
                    if (credentials[0].equals(username) && credentials[1].equals(password))
                        return true;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return false;
        }

        public void wr (String tag, String c){
            try {
                writer.write(tag + " " + c);
                writer.newLine();
                writer.flush();
                System.out.println(tag + " " + c);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private String rd () {
            String result = null;
            try {
                result = reader.readLine();
            } catch (IOException e) {
                System.err.println("Exeption" + e.getMessage());
            }
            if (result == null) {
                //System.out.println("C: NULL");
            } else {
                System.out.println("C: " + result);
                try {
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("mailHeader.txt"));
                    bufferedWriter.write(result);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return result;
        }
    private String getBodyStructureForMail(MimeMessage message) throws MessagingException, IOException {
        StringBuilder result = new StringBuilder("BODYSTRUCTURE (");

        String bodyPartString;
        String boundary;
        try {
            if (message.getContent() instanceof MimeMultipart) {
                Multipart multipart = (Multipart) message.getContent();
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);

                    String mediaType = bodyPart.getContentType().split("/")[0].toUpperCase();
                    String mediaSubtype = bodyPart.getContentType().split("/")[1].split(";")[0].toUpperCase();



                    String text = "";

                    if (mediaSubtype.equalsIgnoreCase("plain")) {
                        text = bodyPart.getContent().toString();
                    } else if (mediaSubtype.equalsIgnoreCase("html")) {
                        text = bodyPart.getContent().toString();
                    } else if (mediaSubtype.equalsIgnoreCase("pdf")) {
                        text = bodyPart.getContent().toString();
                    } else {
                        throw new MessagingException("Unknown media subtype: " + mediaSubtype);
                    }
                    int linesNo = text.split("\n").length; //asta nu stiu exact ce e cu el. Ca nu da ca la exemplu de la gmail ???

                    bodyPartString = String.format(
                            "(\"%s\" \"%s\" (\"CHARSET\" \"UTF-8\") NIL NIL \"BASE64\" %d %d)",
                            mediaType,
                            mediaSubtype,
                            //bodyPart.getHeader("Content-Type")[0],
                            text.length(),
                            linesNo
                    );
                    result.append(bodyPartString);

                }

                if (multipart instanceof MimeMultipart) {
                    boundary = ((MimeMultipart) multipart).getContentType().split("boundary=")[1].replace("\"", "");
                    result.append(String.format(" \"ALTERNATIVE\" (\"BOUNDARY\" \"%s\") NIL NIL)", boundary));
                } else {
                    result.append(")");
                }
            }else {
                result.append("(\"text\" \"plain\" (\"charset\" \"UTF-8\") NIL NIL \"7bit\" 123 1)");
            }

        } catch (IOException | MessagingException e) {

        }

        return result.toString();
    }
}
//        Object content = message.getContent();
//        // Если сообщение состоит из нескольких частей
//        if (content instanceof MimeMultipart) {
//            MimeMultipart multipart = (MimeMultipart) content;
//            int count = multipart.getCount();
//            for (int i = 0; i < count; i++) {
//                BodyPart bodyPart = multipart.getBodyPart(i);
//                return getBodyStructure((MimeBodyPart) bodyPart);
//
////                System.out.println("Body Structure of part " + (i+1) + ":");
////                System.out.println(bodyStructure);
//            }
//        } else if (content instanceof MimeBodyPart) {
//            // Если сообщение состоит из одной части
//            MimeBodyPart bodyPart = (MimeBodyPart) content;
//            return getBodyStructure(bodyPart);
////            System.out.println("Body Structure:");
////            System.out.println(bodyStructure);
//        }
//        return null;
//    }
//
//    private static String getBodyStructure(MimeBodyPart bodyPart) throws MessagingException, IOException {
////        // Получение структуры тела части сообщения
//        StringBuilder bodyStructure = new StringBuilder("BODYSTRUCTURE ");
//
//        // Получение основных полей
//        String contentType = bodyPart.getContentType();
//        String[] parts = contentType.split("; ");
//        String type = parts[0];
//        String subtype = parts[1];
//
//        // Добавление основных полей в структуру
//        bodyStructure.append("(")
//                .append("\"").append(type).append("\"")
//                .append(" \"").append(subtype).append("\"");
//
//        // Добавление дополнительных полей в структуру
//        for (int i = 2; i < parts.length; i++) {
//            String[] attribute = parts[i].split("=");
//            bodyStructure.append(" (\"").append(attribute[0].trim()).append("\"");
//            if (attribute.length > 1) {
//                bodyStructure.append(" \"").append(attribute[1].trim()).append("\"");
//            }
//            bodyStructure.append(")");
//        }
//
//        // Получение размера тела сообщения
//        bodyStructure.append(" ").append(bodyPart.getSize());
//
//        // Получение кодировки тела сообщения
//        bodyStructure.append(" \"").append(bodyPart.getEncoding());
//
//        // Получение дополнительных полей в зависимости от типа части сообщения
//        if (type.startsWith("text")) {
//            // Если тип текстовый
//            bodyStructure.append(" ").append(bodyPart.getContentType());
//            // Добавьте дополнительные поля для текстового сообщения
//        } else if (type.startsWith("multipart")) {
//            // Если тип многократный
//            bodyStructure.append(" (\"").append(subtype).append("\"");
//            // Добавьте дополнительные поля для многократного сообщения
//        } else {
//            // Добавьте дополнительные поля для других типов сообщений
//        }
//
//        // Закрытие структуры
//        bodyStructure.append(")");
//
//        return bodyStructure.toString();

