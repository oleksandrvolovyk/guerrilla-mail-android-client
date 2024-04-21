package volovyk.guerrillamail.data.emails.remote.model

open class RemoteEmailDatabaseException: RuntimeException {
    constructor(message: String) : super(message)
    constructor(throwable: Throwable) : super(throwable)
    constructor() : super()

    data object EmptyResponseException: RemoteEmailDatabaseException()
    data object UnsuccessfulRequestException : RemoteEmailDatabaseException()
    data object NoEmailAddressAssignedException : RemoteEmailDatabaseException()
}