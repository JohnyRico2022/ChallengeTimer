package ru.nikita.challengetimer.common;

public enum ChallengeTarget {
    D1(1), D3(3), D5(5), D7(7),
    D10(10), D15(15), D30(30), D50(50),
    D75(75), D100(100), D150(150), D200(200),
    D250(250), D300(300), D350(350), D365(365);

    public final int days;

    ChallengeTarget(int days) {
        this.days = days;
    }

    /// Для перебора
    public static final ChallengeTarget[] ALL = values();
}