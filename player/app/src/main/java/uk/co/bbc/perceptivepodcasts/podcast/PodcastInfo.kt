package uk.co.bbc.perceptivepodcasts.podcast

data class Credit(var name: String)
data class CreditGroup(var name: String, var credits: List<Credit>)
data class PodcastInfo(val title: String, val creditGroups: List<CreditGroup>)
