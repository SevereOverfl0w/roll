(ns roll.modules.asg)

;; Specify? i.e. show availability-zones is a list

(defn generate [{:keys [environment ami instance-type security-group-id iam-instance-profile user-data key-name availability-zones instance-count target-group-arns]
                 :or {instance-count "2"
                      target-group-arns []
                      user-data ""}}]
  (let [app-name :app]
    {:aws_launch_configuration
     {environment
      {:name_prefix           environment
       :image_id              ami
       :instance_type         instance-type
       :security_groups       [security-group-id]
       :iam_instance_profile  iam-instance-profile
       :user_data             user-data
       :key_name              key-name
       :lifecycle {:create_before_destroy  false}}}

     :aws_autoscaling_group
     {environment
      {:availability_zones    availability-zones
       :name                  environment
       :max_size              (str instance-count)
       :min_size              (str instance-count)
       :launch_configuration  (str "${aws_launch_configuration." (clojure.string/replace environment "-" "_") ".name}")
       :target_group_arns     target-group-arns

       :tag [{:key "Name"
              :value (str environment "-" (name app-name))
              :propagate_at_launch true}]

       :lifecycle {:create_before_destroy true}}}}))
