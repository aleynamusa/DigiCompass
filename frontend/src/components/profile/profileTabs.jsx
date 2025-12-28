import { Tabs } from "@mantine/core";
import { Heart, Share2, MapPin } from "lucide-react";
import ProfileSharedRoutes from "@/components/profile/profileSharedRoutes.jsx";
import ProfileLikedRoutes from "@/components/profile/profileLikedRoutes.jsx";

export default function ProfileTabs({
  isOwnProfile,
  baseRoutes,
  likedRoutes,
  onFetchLiked,
  onViewDetails
}) {
  return (
    <Tabs defaultValue="shared" variant="pills">
      <Tabs.List>
        <Tabs.Tab value="shared" leftSection={<Share2 size={16} />}>
          Shared Routes
        </Tabs.Tab>

        {isOwnProfile && (
          <Tabs.Tab value="liked" leftSection={<Heart size={16} />} onClick={onFetchLiked}>
            Liked Routes
          </Tabs.Tab>
        )}

        <Tabs.Tab value="all" leftSection={<MapPin size={16} />}>
          All Routes
        </Tabs.Tab>
      </Tabs.List>

      <Tabs.Panel value="shared" pt="lg">
        <ProfileSharedRoutes
          routes={baseRoutes}
          onViewDetails={onViewDetails}
          isOwnProfile={isOwnProfile}
        />
      </Tabs.Panel>

      {isOwnProfile && (
          <Tabs.Panel value="liked" pt="lg">
            <ProfileLikedRoutes likedRoutes={likedRoutes} onViewDetails={onViewDetails} />
          </Tabs.Panel>
      )}

      <Tabs.Panel value="all" pt="lg">
        <div className="text-center py-12">
          <MapPin size={48} className="mx-auto mb-4" />
          <p>No routes yet</p>
        </div>
      </Tabs.Panel>
    </Tabs>
  );
}
