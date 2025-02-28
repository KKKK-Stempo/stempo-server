// scripts/common/slackNotifier.groovy
import groovy.json.JsonOutput

/**
 * Slack에 빌드 알림을 보내는 함수.
 *
 * @param message : 전송 메시지 (String)
 * @param color : 메시지 색상 (String)
 * @param jobUrl : Job URL (String)
 * @param consoleOutputUrl : 콘솔 출력 URL (String)
 * @param slackUrl : Slack Webhook URL (String)
 * @param changeLog : Git 변경 내역 (String, 선택)
 */
def sendSlackBuildNotification(Map params = [:]) {
    if (!params.message || !params.color || !params.jobUrl || !params.consoleOutputUrl || !params.slackUrl) {
        error "sendSlackBuildNotification: 필수 파라미터가 누락되었습니다."
    }
    def payload = createSlackPayload(params.message, params.color, params.jobUrl, params.consoleOutputUrl, params.changeLog)
    def payloadJson = JsonOutput.toJson(payload)
    sendHttpPostRequest(params.slackUrl, payloadJson)
}

def createSlackPayload(String message, String color, String jobUrl, String consoleOutputUrl, String changeLog = "") {
    return [
        blocks     : [
            [
                type: "section",
                text: [type: "mrkdwn", text: message]
            ]
        ],
        attachments: [
            [
                color : color,
                blocks: [
                    [
                        type: "section",
                        text: [type: "mrkdwn", text: "*Change Log:*\n${changeLog}"]
                    ],
                    [
                        type    : "actions",
                        elements: [
                            [
                                type : "button",
                                text : [type: "plain_text", text: "Job", emoji: true],
                                url  : jobUrl,
                                value: "click_job"
                            ],
                            [
                                type : "button",
                                text : [type: "plain_text", text: "Console Output", emoji: true],
                                url  : consoleOutputUrl,
                                value: "click_console_output"
                            ]
                        ]
                    ]
                ]
            ]
        ]
    ]
}

def sendHttpPostRequest(String url, String payload) {
    def CONTENT_TYPE_JSON = 'application/json'
    def HTTP_POST = 'POST'
    sh """
        curl -X ${HTTP_POST} \\
            -H 'Content-type: ${CONTENT_TYPE_JSON}' \\
            --data '${payload}' \\
            ${url}
    """
}

return this
