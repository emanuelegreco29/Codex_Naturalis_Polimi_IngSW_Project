module it.polimi.ingsw.am43 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.fasterxml.jackson.databind;
    requires java.rmi;
    requires java.desktop;

    exports it.polimi.ingsw.am43.Controller;
    opens it.polimi.ingsw.am43.Controller to javafx.fxml;
    exports it.polimi.ingsw.am43.Main;
    opens it.polimi.ingsw.am43.Main to javafx.fxml;

    exports it.polimi.ingsw.am43.View.GUI to javafx.graphics;
    opens it.polimi.ingsw.am43.View.GUI to javafx.fxml;
    exports it.polimi.ingsw.am43.View.TUI to javafx.graphics;
    opens it.polimi.ingsw.am43.View.TUI to javafx.fxml;

    exports it.polimi.ingsw.am43.Network.RMI to java.rmi;
    exports it.polimi.ingsw.am43.Network to java.rmi;
    exports it.polimi.ingsw.am43.Network.Socket.Heartbeat to java.rmi;

    exports it.polimi.ingsw.am43.Network.Messages.toClientMessages;
    exports it.polimi.ingsw.am43.Network.Messages.controllerMessages;
    exports it.polimi.ingsw.am43.Network.Messages;
    exports it.polimi.ingsw.am43.Model.Cards;
    exports it.polimi.ingsw.am43.Model.Points;
    exports it.polimi.ingsw.am43.Model.Enum;
    exports it.polimi.ingsw.am43.Model;
}
