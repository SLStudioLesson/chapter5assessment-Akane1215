package com.taskapp.logic;

import java.time.LocalDate;
import java.util.List;

import com.taskapp.dataaccess.LogDataAccess;
import com.taskapp.dataaccess.TaskDataAccess;
import com.taskapp.dataaccess.UserDataAccess;
import com.taskapp.exception.AppException;
import com.taskapp.model.Log;
import com.taskapp.model.Task;
import com.taskapp.model.User;

public class TaskLogic {
    private final TaskDataAccess taskDataAccess;
    private final LogDataAccess logDataAccess;
    private final UserDataAccess userDataAccess;


    public TaskLogic() {
        taskDataAccess = new TaskDataAccess();
        logDataAccess = new LogDataAccess();
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param taskDataAccess
     * @param logDataAccess
     * @param userDataAccess
     */
    public TaskLogic(TaskDataAccess taskDataAccess, LogDataAccess logDataAccess, UserDataAccess userDataAccess) {
        this.taskDataAccess = taskDataAccess;
        this.logDataAccess = logDataAccess;
        this.userDataAccess = userDataAccess;
    }

    /**
     * 全てのタスクを表示します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findAll()
     * @param loginUser ログインユーザー
     */
    public void showAll(User loginUser) {
        // findAllメソッドを実行して、データの一覧を取得
        List<Task> tasks = taskDataAccess.findAll();

        // 取得したデータを表示する
        tasks.forEach(task -> {
            String status = " ";
            if (task.getStatus() == 0) {
                status = "未着手";
            } else if (task.getStatus() == 1) {
                status = "着手中";
            } else if (task.getStatus() == 2) {
                status = "完了";
            }

        // 担当者情報を取得する
            String staffInfo;
            if (task.getRepUser().getCode() == loginUser.getCode()) {
                staffInfo = "あなたが担当しています";
            } else {
                staffInfo = task.getRepUser().getName() + "が担当しています";
            }

            System.out.println(task.getCode() + "." + "タスク名：" + task.getName() +
            "担当者名：" + staffInfo + "ステータス：" + status);
        });

    }

    /**
     * 新しいタスクを保存します。
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#save(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param name タスク名
     * @param repUserCode 担当ユーザーコード
     * @param loginUser ログインユーザー
     * @throws AppException ユーザーコードが存在しない場合にスローされます
     */
    public void save(int code, String name, int repUserCode,
                    User loginUser) throws AppException {
        
        User repUser = userDataAccess.findByCode(repUserCode);
            if (repUser == null) {
                throw new AppException("存在するユーザーコードを入力してください");
            }


        // 入力値をTaskオブジェクトにマッピング
        Task task = new Task(code, name, repUserCode, loginUser);
        // saveメソッドを呼び出して、入力されたデータを保存
        taskDataAccess.save(task);
        System.out.println(task.getName() + "の登録が完了しました");

    }

    /**
     * タスクのステータスを変更します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#update(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param status 新しいステータス
     * @param loginUser ログインユーザー
     * @throws AppException タスクコードが存在しない、またはステータスが前のステータスより1つ先でない場合にスローされます
     */
    public void changeStatus(int code, int status,
                            User loginUser) throws AppException {

        Task existingTask = taskDataAccess.findByCode(code);
        if (existingTask == null) {
            throw new AppException("存在するタスクコードを入力してください");
        }
        // 入力値をオブジェクトにマッピング
        Task updatedTask = new Task(code, existingTask.getName(), status, loginUser);

        taskDataAccess.update(updatedTask);
    }

    /**
     * タスクを削除します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#delete(int)
     * @see com.taskapp.dataaccess.LogDataAccess#deleteByTaskCode(int)
     * @param code タスクコード
     * @throws AppException タスクコードが存在しない、またはタスクのステータスが完了でない場合にスローされます
     */
    // public void delete(int code) throws AppException {
    // }
}