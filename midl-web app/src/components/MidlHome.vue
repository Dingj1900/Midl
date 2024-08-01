<script setup>
import WelcomeItem from './WelcomeItem.vue'
import DocumentationIcon from './icons/IconDocumentation.vue'
import ToolingIcon from './icons/IconTooling.vue'
import EcosystemIcon from './icons/IconEcosystem.vue'
import CommunityIcon from './icons/IconCommunity.vue'
import SupportIcon from './icons/IconSupport.vue'
</script>

<template>
  <WelcomeItem>
    <template #icon>
      <DocumentationIcon />
    </template>
    <template #heading>Midl - Your Ultimate Event Coordination Companion</template>

    Midl is an innovative app designed to simplify event planning by calculating the optimal midpoint among multiple locations based on equal drive time. Whether you're organizing weddings, corporate events, conferences, golf trips, or meet-ups, Midl takes the stress out of location planning. Powered by the Google Maps API, Midl ensures accurate geolocation data and provides a visual map display of the midpoint, making it easier than ever for attendees coming from different places to converge at a central location.
  </WelcomeItem>

  <WelcomeItem>
    <template #icon>
      <ToolingIcon />
    </template>
    <template #heading>Effortless Address Input and Midpoint Calculation</template>

    Midl allows users to effortlessly input multiple addresses, making the process of finding a central meeting point straightforward and efficient. Midl calculates the central point where all attendees can meet with equal travel time and displays this on an interactive map. With user accounts, saved locations, shareable invitations, and adjustable travel preferences, Midl ensures a seamless and customized event planning experience.

    <br />

    More instructions are available in <code>README.md</code>.
  </WelcomeItem>

  <WelcomeItem>
    <template #icon>
      <EcosystemIcon />
    </template>
    <template #heading>Step-by-Step Guide to Using Midl</template>

    Using Midl is simple: input multiple addresses, and Midl uses the Google Maps API to convert these addresses into geolocation coordinates. It then calculates travel times using the Distance Matrix API and identifies the optimal midpoint. The interactive map displays the midpoint and markers for each address. With reliable backend logic and data storage, Midl guarantees a smooth and efficient event planning process, from initial setup to final execution.
  </WelcomeItem>

  <!-- <WelcomeItem>
    <template #icon>
      <CommunityIcon />
    </template>
    <template #heading>Community</template>

    Got stuck? Ask your question on
    <a href="https://chat.vuejs.org" target="_blank" rel="noopener">Vue Land</a>, our official
    Discord server, or
    <a href="https://stackoverflow.com/questions/tagged/vue.js" target="_blank" rel="noopener"
      >StackOverflow</a
    >. You should also subscribe to
    <a href="https://news.vuejs.org" target="_blank" rel="noopener">our mailing list</a> and follow
    the official
    <a href="https://twitter.com/vuejs" target="_blank" rel="noopener">@vuejs</a>
    twitter account for latest news in the Vue world.
  </WelcomeItem>

  <WelcomeItem>
    <template #icon>
      <SupportIcon />
    </template>
    <template #heading>Support Vue</template>

    As an independent project, Vue relies on community backing for its sustainability. You can help
    us by
    <a href="https://vuejs.org/sponsor/" target="_blank" rel="noopener">becoming a sponsor</a>.
  </WelcomeItem> -->
</template>
