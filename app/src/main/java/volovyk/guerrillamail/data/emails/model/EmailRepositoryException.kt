package volovyk.guerrillamail.data.emails.model

sealed class EmailRepositoryException : RuntimeException {
    constructor(throwable: Throwable) : super(throwable)
    constructor() : super()

    class EmailAddressAssignmentException(throwable: Throwable) : EmailRepositoryException(throwable)
    class EmailFetchException(throwable: Throwable) : EmailRepositoryException(throwable)
}