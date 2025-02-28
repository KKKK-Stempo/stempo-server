// cd/common/config.groovy
return [
    // 공통 변경을 감지할 경로(이 경로에 변경이 있으면 전체 빌드를 트리거)
    runAllLocations: ['cd/'],

    // 서비스별 폴더 매핑: 각 키에 해당하는 폴더 목록이 변경되면 해당 서비스로 간주
    servicesMapping: [
        core: [
            'stempo-api',
            'stempo-application',
            'stempo-auth',
            'stempo-common',
            'stempo-domain',
            'stempo-infrastructure'
        ],
        rhythm: [
            'stempo-rhythm'
        ]
    ]
]
