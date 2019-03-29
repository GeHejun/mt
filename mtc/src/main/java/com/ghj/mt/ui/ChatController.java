package com.ghj.mt.ui;


import com.ghj.mt.dto.UserDTO;
import com.ghj.mt.message.Message;
import com.ghj.mt.message.MessageType;
import com.ghj.mt.message.User;
import com.ghj.mt.ui.bubble.BubbleSpec;
import com.ghj.mt.ui.bubble.BubbledLabel;
import com.ghj.mt.util.VoicePlayback;
import com.ghj.mt.util.VoiceRecorder;
import com.ghj.mt.util.VoiceUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class ChatController implements Initializable {

    @FXML private TextArea messageBox;
    @FXML private ListView userList;
    @FXML private ImageView userImageView;
    @FXML ListView chatPane;
    @FXML BorderPane borderPane;
    @FXML ImageView microphoneImageView;
    private Integer userName;

    Image microphoneActiveImage = new Image(getClass().getClassLoader().getResource("images/microphone-active.png").toString());
    Image microphoneInactiveImage = new Image(getClass().getClassLoader().getResource("images/microphone.png").toString());

    private double xOffset;
    private double yOffset;


    public void sendButtonAction()  {
        String msg = messageBox.getText();
        if (!messageBox.getText().isEmpty()) {
            messageBox.clear();
        }
        Message message = new Message();
        message.setBody(msg);
        message.setMsgType(MessageType.MESSAGE);
        message.setClientID(userName);
        message.setUserDTO(UserDTO.builder().clientId(userName).build());
        addToChat(message);
    }

    public void recordVoiceMessage()  {
        if (VoiceUtil.isRecording()) {
            Platform.runLater(() -> microphoneImageView.setImage(microphoneInactiveImage));
            VoiceUtil.setRecording(false);
        } else {
            Platform.runLater(() -> {
                        microphoneImageView.setImage(microphoneActiveImage);

                    }
            );
            VoiceRecorder.captureAudio();
        }
    }
    public void setUserList(Message msg) {
        Platform.runLater(() -> {
            ObservableList<User> users = FXCollections.observableList(msg.getUserDTO().getFriendList());
            userList.setItems(users);
            userList.setCellFactory(new CellRender());
        });
    }


    public synchronized void addToChat(Message msg) {
        Task<HBox> othersMessages = new Task<HBox>() {
            @Override
            public HBox call() throws Exception {
                Image image = new Image(getClass().getClassLoader().getResource("images/" + msg.getPicture().toLowerCase() + ".png").toString());
                ImageView profileImage = new ImageView(image);
                profileImage.setFitHeight(32);
                profileImage.setFitWidth(32);
                BubbledLabel bl6 = new BubbledLabel();
                if (msg.getMsgType() == MessageType.VOICE){
                    ImageView imageview = new ImageView(new Image(getClass().getClassLoader().getResource("images/sound.png").toString()));
                    bl6.setGraphic(imageview);
                    bl6.setText("Sent a voice message!");
                    VoicePlayback.playAudio(msg.getVoiceMsg());
                }else {
                    bl6.setText(msg.getClientID() + ": " + msg.getBody());
                }
                bl6.setBackground(new Background(new BackgroundFill(Color.WHITE,null, null)));
                HBox x = new HBox();
                bl6.setBubbleSpec(BubbleSpec.FACE_LEFT_CENTER);
                x.getChildren().addAll(profileImage, bl6);
                return x;
            }
        };

        othersMessages.setOnSucceeded(event -> {
            chatPane.getItems().add(othersMessages.getValue());
        });

        Task<HBox> yourMessages = new Task<HBox>() {
            @Override
            public HBox call() throws Exception {
                Image image = userImageView.getImage();
                ImageView profileImage = new ImageView(image);
                profileImage.setFitHeight(32);
                profileImage.setFitWidth(32);

                BubbledLabel bl6 = new BubbledLabel();
                if (msg.getMsgType() == MessageType.VOICE){
                    bl6.setGraphic(new ImageView(new Image(getClass().getClassLoader().getResource("images/sound.png").toString())));
                    bl6.setText("Sent a voice message!");
                    VoicePlayback.playAudio(msg.getVoiceMsg());
                }else {
                    bl6.setText(msg.getBody());
                }
                bl6.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN,
                        null, null)));
                HBox x = new HBox();
                x.setMaxWidth(chatPane.getWidth() - 20);
                x.setAlignment(Pos.TOP_RIGHT);
                bl6.setBubbleSpec(BubbleSpec.FACE_RIGHT_CENTER);
                x.getChildren().addAll(bl6, profileImage);
                return x;
            }
        };
        yourMessages.setOnSucceeded(event -> chatPane.getItems().add(yourMessages.getValue()));

        if (msg.getClientID().equals(userName)) {
            Thread t2 = new Thread(yourMessages);
            t2.setDaemon(true);
            t2.start();
        } else {
            Thread t = new Thread(othersMessages);
            t.setDaemon(true);
            t.start();
        }
    }

    public void setUserName(Integer userName) {
        this.userName = userName;
    }

    public void setImageLabel()  {
        this.userImageView.setImage(new Image(getClass().getClassLoader().getResource("images/dominic.png").toString()));
    }


    public void sendMethod(KeyEvent event)  {
        if (event.getCode() == KeyCode.ENTER) {
            sendButtonAction();
        }
    }

    @FXML
    public void closeApplication() {
        Platform.exit();
        System.exit(0);
    }

    /* Method to display server messages */
    public synchronized void addAsServer(Message msg) {
        Task<HBox> task = new Task<HBox>() {
            @Override
            public HBox call() throws Exception {
                BubbledLabel bl6 = new BubbledLabel();
                bl6.setText(msg.getBody());
                bl6.setBackground(new Background(new BackgroundFill(Color.ALICEBLUE,
                        null, null)));
                HBox x = new HBox();
                bl6.setBubbleSpec(BubbleSpec.FACE_BOTTOM);
                x.setAlignment(Pos.CENTER);
                x.getChildren().addAll(bl6);
                return x;
            }
        };
        task.setOnSucceeded(event -> {
            chatPane.getItems().add(task.getValue());
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setImageLabel();

        /* Drag and Drop */
        borderPane.setOnMousePressed(event -> {
            xOffset = MainLauncher.getPrimaryStageObj().getX() - event.getScreenX();
            yOffset = MainLauncher.getPrimaryStageObj().getY() - event.getScreenY();
            borderPane.setCursor(Cursor.CLOSED_HAND);
        });

        borderPane.setOnMouseDragged(event -> {
            MainLauncher.getPrimaryStageObj().setX(event.getScreenX() + xOffset);
            MainLauncher.getPrimaryStageObj().setY(event.getScreenY() + yOffset);

        });

        borderPane.setOnMouseReleased(event -> {
            borderPane.setCursor(Cursor.DEFAULT);
        });


        /* Added to prevent the enter from adding a new line to inputMessageBox */
        messageBox.addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                sendButtonAction();
                ke.consume();
            }
        });

    }


    public void logoutScene() {
        Platform.runLater(() -> {
            FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
            Parent window = null;
            try {
                window = (Pane) fmxlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage stage = MainLauncher.getPrimaryStageObj();
            Scene scene = new Scene(window);
            stage.setMaxWidth(350);
            stage.setMaxHeight(420);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.centerOnScreen();
        });
    }
}
